package Radium.Engine.Components.Physics;

import Radium.Editor.Annotations.HideInEditor;
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
import physx.common.PxBaseFlagEnum;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geomutils.*;
import physx.physics.*;

/**
 * A physics component that contains features such as gravity and collision
 */
public class Rigidbody extends Component {

    /**
     * Mass of object
     */
    public float mass = 1f;

    public float drag = 0.1f;
    public float angularDrag = 0.1f;

    /**
     * Determines whether gravity is applied on object
     */
    public boolean applyGravity = true;

    /**
     * Type of collider shape the object uses
     */
    public ColliderType collider = ColliderType.Box;
    /**
     * Show collider in editor
     */
    public boolean showCollider = true;

    public PhysicsMaterial physicsMaterial = new PhysicsMaterial();

    private transient PxRigidBody body;

    @HideInEditor
    public float radius = 0.5f;
    @HideInEditor
    public Vector3 colliderScale = Vector3.One();

    private transient ColliderGizmo gizmo;

    /**
     * Create empty rigidbody component
     */
    public Rigidbody() {
        description = "A body that handles collisions and physics";
        impact = PerformanceImpact.Low;
        icon = new Texture("EngineAssets/Editor/Icons/rigidbody.png", true).GetTextureID();
        submenu = "Physics";
    }

    public void Start() {
        if (!applyGravity) body.setLinearVelocity(new PxVec3(0, 0, 0));
        UpdateBodyTransform();
    }

    public void Update() {
        PxActorFlags flags = body.getActorFlags();
        if (applyGravity) {
            flags.clear(PxActorFlagEnum.eDISABLE_GRAVITY);
        } else {
            flags.set(PxActorFlagEnum.eDISABLE_GRAVITY);
        }
        body.setActorFlags(flags);

        gameObject.transform.localPosition = PhysxUtil.FromPx3(body.getGlobalPose().getP());
        gameObject.transform.localRotation = PhysxUtil.GetEuler(body.getGlobalPose().getQ());
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
        if (DidFieldChange(update, "mass")) {
            body.setMass(mass);
        } else if (DidFieldChange(update, "collider")) {
            UpdateBody();
        } else if (DidFieldChange(update, "drag")) {
            body.setLinearDamping(drag);
        } else if (DidFieldChange(update, "angularDrag")) {
            body.setAngularDamping(angularDrag);
        } else if (DidFieldChange(update, "colliderScale")) {
            CreateBody();
        } else if (DidFieldChange(update, "radius")) {
            CreateBody();
        } else if (DidFieldChange(update, "physicsMaterial")) {
            CreateBody();
        } else if (DidFieldChange(update, "applyGravity")) {
            if (applyGravity) body.getActorFlags().set(PxActorFlagEnum.eDISABLE_GRAVITY);
            else body.getActorFlags().clear(PxActorFlagEnum.eDISABLE_GRAVITY);
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
        body.setMass(mass);
    }

    public void UpdateBodyTransform() {
        PxTransform tmpPose = new PxTransform(PhysxUtil.ToPx3(gameObject.transform.WorldPosition()), PhysxUtil.SetEuler(gameObject.transform.WorldRotation()));
        body.setGlobalPose(tmpPose);
    }

    /**
     * Returns the Nvidia PhysX Dynamic Rigidbody
     * @return Nvidia PhysX Dynamic Rigidbody
     */
    public PxRigidBody GetBody() {
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

    public void CreateBody() {
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
        body = PhysicsManager.GetPhysics().createRigidDynamic(tmpPose);
        shape.setSimulationFilterData(tmpFilterData);

        body.attachShape(shape);
        body.setMass(mass);
        body.setName(gameObject.id);

        body.setLinearDamping(drag);
        body.setAngularDamping(angularDrag);

        if (!applyGravity) {
            body.getActorFlags().set(PxActorFlagEnum.eDISABLE_GRAVITY);
        }

        shape.release();

        PhysicsManager.GetPhysicsScene().addActor(body);
    }

    private void ResetBody() {
        body.setGlobalPose(new PxTransform(PhysxUtil.ToPx3(gameObject.transform.WorldPosition()), PhysxUtil.SetEuler(gameObject.transform.WorldRotation())));
        body.setLinearVelocity(new PxVec3(0, 0, 0));
        body.setAngularVelocity(new PxVec3(0, 0, 0));
    }

    /**
     * Adds a force to an object
     * @param force Strength of force
     */
    public void AddForce(Vector3 force) {
        body.addForce(PhysxUtil.ToPx3(force));
    }

    public void AddForce(float x, float y, float z) {
        body.addForce(new PxVec3(x, y, z));
    }

    /**
     * Adds a force to an object
     * @param force Strength of force
     * @param forceMode The type of force that is used on the object
     */
    public void AddForce(Vector3 force, ForceMode forceMode) {
        int mode = switch (forceMode) {
            case Force -> PxForceModeEnum.eFORCE;
            case Impulse -> PxForceModeEnum.eIMPULSE;
            default -> PxForceModeEnum.eACCELERATION;
        };

        body.addForce(PhysxUtil.ToPx3(force), mode);
    }

    /**
     * Adds torque to an object
     * @param torque The strength of torque
     */
    public void AddTorque(Vector3 torque) {
        body.addTorque(PhysxUtil.ToPx3(torque));
    }

    public void AddTorque(float x, float y, float z) {
        body.addTorque(new PxVec3(x, y, z));
    }

    /**
     * Sets the rigidbodies velocity
     * @param velocity The new velocity
     */
    public void SetVelocity(Vector3 velocity) {
        body.setLinearVelocity(new PxVec3(velocity.x, velocity.y, velocity.z));
    }

    public void SetVelocity(float x, float y, float z) {
        body.setLinearVelocity(new PxVec3(x, y, z));
    }

    /**
     * Sets the rigidbodies angular velocity
     * @param velocity The new angular velocity
     */
    public void SetAngularVelocity(Vector3 velocity) {
        body.setAngularVelocity(new PxVec3(velocity.x, velocity.y, velocity.z));
    }

    public void SetAngularVelocity(float x, float y, float z) {
        body.setAngularVelocity(new PxVec3(x, y, z));
    }

    public Vector3 GetVelocity() {
        PxVec3 vel = body.getLinearVelocity();
        return new Vector3(vel.getX(), vel.getY(), vel.getZ());
    }

    public Vector3 GetAngularVelocity() {
        PxVec3 vel = body.getAngularVelocity();
        return new Vector3(vel.getX(), vel.getY(), vel.getZ());
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
