package Radium.Engine.Components.Physics;

import Radium.Editor.Annotations.HideInEditor;
import Radium.Editor.Annotations.RunInEditMode;
import Radium.Editor.Console;
import Radium.Editor.Debug.Debug;
import Radium.Engine.Component;
import Radium.Editor.Debug.Gizmo.ColliderGizmo;
import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.PerformanceImpact;
import Radium.Engine.Physics.*;
import imgui.ImGui;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geomutils.*;
import physx.physics.*;

@RunInEditMode
public class StaticRigidbody extends Component {

    /**
     * Type of collider shape the object uses
     */
    public ColliderType collider = ColliderType.Box;
    /**
     * Show collider in editor
     */
    public boolean showCollider = true;

    public PhysicsMaterial physicsMaterial = new PhysicsMaterial();

    private transient PxRigidStatic body;

    @HideInEditor
    public float radius = 0.5f;
    @HideInEditor
    public Vector3 colliderScale = Vector3.One();

    private transient ColliderGizmo gizmo;

    /**
     * Create empty rigidbody component
     */
    public StaticRigidbody() {
        description = "A body that handles collisions and physics";
        impact = PerformanceImpact.Low;
        icon = new Texture("EngineAssets/Editor/Icons/rigidbody.png").GetTextureID();
        submenu = "Physics";
        name = "Static Rigidbody";
    }

    public void Start() {
        UpdateBodyTransform();
    }

    public void Update() {
        UpdateBodyTransform();
    }

    public void Stop() {
        CreateBody();
    }

    public void OnAdd() {
        CreateBody();
        gizmo = new ColliderGizmo(this);
    }

    public void OnRemove() {
        gizmo.Destroy();
        PhysicsManager.GetPhysicsScene().removeActor(body);
    }

    public void UpdateVariable(String update) {
        if (DidFieldChange(update, "collider")) {
            UpdateBody();
        } else if (DidFieldChange(update, "colliderScale")) {
            CreateBody();
        } else if (DidFieldChange(update, "radius")) {
            CreateBody();
        } else if (DidFieldChange(update, "physicsMaterial")) {
            CreateBody();
        }

        gizmo.UpdateCollider();
    }

    public void GUIRender() {
        if (collider == ColliderType.Box) {
            float[] imVec = { colliderScale.x, colliderScale.y, colliderScale.z };
            if (ImGui.dragFloat3("Collider Scale", imVec)) {
                colliderScale.Set(imVec[0], imVec[1], imVec[2]);

                UpdateBody();
            }
        } else if (collider == ColliderType.Sphere) {
            float[] imFloat = { radius };
            if (ImGui.dragFloat("Collider Radius", imFloat)) {
                radius = imFloat[0];

                UpdateBody();
            }
        }
    }

    /**
     * Recreates physics body
     */
    public void UpdateBody() {
        CreateBody();
        gizmo.UpdateCollider();
    }

    public void UpdateBodyTransform() {
        PxTransform tmpPose = new PxTransform(PhysxUtil.ToPx3(gameObject.transform.WorldPosition()), PhysxUtil.SetEuler(gameObject.transform.WorldRotation()));
        body.setGlobalPose(tmpPose);
    }

    /**
     * Returns the Nvidia PhysX Dynamic Rigidbody
     * @return Nvidia PhysX Dynamic Rigidbody
     */
    public PxRigidStatic GetBody() {
        return body;
    }

    public void SetRadius(float radius) {
        this.radius = radius;
        UpdateBody();
    }

    public void SetScale(Vector3 scale) {
        this.colliderScale = scale;
        UpdateBody();
    }

    private void CreateBody() {
        if (body != null) {
            PhysicsManager.GetPhysicsScene().removeActor(body);
        }
        body = null;

        PxMaterial material = PhysicsManager.GetPhysics().createMaterial(physicsMaterial.friction, physicsMaterial.friction, physicsMaterial.restitution);
        PxShapeFlags shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE));
        PxTransform tmpPose = new PxTransform(PhysxUtil.ToPx3(gameObject.transform.WorldPosition()), PhysxUtil.SetEuler(gameObject.transform.WorldRotation()));
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        PxGeometry geometry = null;
        Vector3 scale = gameObject.transform.WorldScale();
        if (collider == ColliderType.Box) {
            geometry = new PxBoxGeometry((colliderScale.x * scale.x), (colliderScale.y * scale.y), (colliderScale.z * scale.z));
        } else if (collider == ColliderType.Sphere) {
            geometry = new PxSphereGeometry(radius * scale.x * 2);
        } else if (collider == ColliderType.Mesh) {
            MeshFilter mf = gameObject.GetComponent(MeshFilter.class);
            if (mf == null || mf.mesh == null) {
                Console.Error("Please add a mesh filter or add a mesh");
                collider = ColliderType.Box;
                CreateBody();
                return;
            }

            geometry = Colliders.TriangleMeshCollider(gameObject, mf.mesh);
        }

        PxShape shape = PhysicsManager.GetPhysics().createShape(geometry, material, true, shapeFlags);
        body = PhysicsManager.GetPhysics().createRigidStatic(tmpPose);
        shape.setSimulationFilterData(tmpFilterData);

        body.attachShape(shape);
        body.setName(gameObject.id);

        shape.release();

        PhysicsManager.GetPhysicsScene().addActor(body);
    }

    private void ResetBody() {
        body.setGlobalPose(new PxTransform(PhysxUtil.ToPx3(gameObject.transform.WorldPosition()), PhysxUtil.SetEuler(gameObject.transform.WorldRotation())));
    }

    /**
     * Sets the colliders radius
     * @param radius The new collider radius
     */
    public void SetColliderRadius(float radius) {
        this.radius = radius;

        UpdateBody();
    }

    /**
     * Sets the collider scale
     * @param colliderScale The new collider scale
     */
    public void SetColliderScale(Vector3 colliderScale) {
        this.colliderScale = colliderScale;

        UpdateBody();
    }

    /**
     * Returns the collider radius
     * @return Collider radius
     */
    public float GetColliderRadius() {
        return radius;
    }

    /**
     * Returns the collider scale
     * @return Collider scale
     */
    public Vector3 GetColliderScale() {
        return colliderScale;
    }
}
