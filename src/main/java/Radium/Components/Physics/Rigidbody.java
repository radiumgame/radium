package Radium.Components.Physics;

import Radium.Component;
import RadiumEditor.Debug.Gizmo.ColliderGizmo;
import Radium.Graphics.Texture;
import Radium.Math.Vector.Vector3;
import Radium.PerformanceImpact;
import Radium.Physics.*;
import imgui.ImGui;
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
    /**
     * Determines whether gravity is applied on object
     */
    public boolean applyGravity = true;
    /**
     * Locks the position of the object
     */
    public boolean lockPosition;
    /**
     * Locks the rotation of the object
     */
    public boolean lockRotation;

    /**
     * Type of collider shape the object uses
     */
    public ColliderType collider = ColliderType.Box;
    /**
     * Show collider in editor
     */
    public boolean showCollider = true;

    private transient PxRigidDynamic body;

    private float radius = 0.5f;
    private Vector3 colliderScale = Vector3.One();

    private transient ColliderGizmo gizmo;

    /**
     * Create empty rigidbody component
     */
    public Rigidbody() {
        description = "A body that handles collisions and physics";
        impact = PerformanceImpact.Medium;
        icon = new Texture("EngineAssets/Editor/Icons/rigidbody.png").textureID;
        submenu = "Physics";
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        if (!applyGravity) {
            body.setLinearVelocity(new PxVec3(0, 0, 0));
        }

        if (lockPosition) {
            body.setLinearVelocity(new PxVec3(0, 0, 0));
            body.setMaxLinearVelocity(0);
        }
        if (lockRotation) {
            body.setAngularVelocity(new PxVec3(0, 0, 0));
            body.setMaxAngularVelocity(0);
        }

        gameObject.transform.localPosition = PhysxUtil.FromPx3(body.getGlobalPose().getP());
        gameObject.transform.localRotation = PhysxUtil.GetEuler(body.getGlobalPose().getQ());
    }

    @Override
    public void Stop() {
        ResetBody();
    }

    @Override
    public void OnAdd() {
        CreateBody();

        gizmo = new ColliderGizmo(this);
    }

    @Override
    public void OnRemove() {
        gizmo.Destroy();
    }

    @Override
    public void UpdateVariable() {
        UpdateBody();
    }

    @Override
    public void GUIRender() {
        if (collider == ColliderType.Box) {
            float[] imVec = { colliderScale.x, colliderScale.y, colliderScale.z };
            if (ImGui.dragFloat3("Collider Scale", imVec)) {
                colliderScale.Set(imVec[0], imVec[1], imVec[2]);

                UpdateVariable();
            }
        } else if (collider == ColliderType.Sphere) {
            float[] imFloat = { radius };
            if (ImGui.dragFloat("Collider Radius", imFloat)) {
                radius = imFloat[0];

                UpdateVariable();
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

    /**
     * Returns the Nvidia PhysX Dynamic Rigidbody
     * @return Nvidia PhysX Dynamic Rigidbody
     */
    public PxRigidDynamic GetBody() {
        return body;
    }

    private void CreateBody() {
        if (body != null) {
            PhysicsManager.GetPhysicsScene().removeActor(body);
        }
        body = null;

        PxMaterial material = PhysicsManager.GetPhysics().createMaterial(0.5f, 0.5f, 0.5f);
        PxShapeFlags shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE));
        PxTransform tmpPose = new PxTransform(PhysxUtil.ToPx3(gameObject.transform.localPosition), PhysxUtil.SetEuler(gameObject.transform.localRotation));
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        PxGeometry geometry = null;
        Vector3 scale = gameObject.transform.localScale;
        if (collider == ColliderType.Box) {
            geometry = new PxBoxGeometry((colliderScale.x / 2) * scale.x, (colliderScale.y / 2) * scale.y, (colliderScale.z / 2) * scale.z);
        } else if (collider == ColliderType.Sphere) {
            geometry = new PxSphereGeometry(radius);
        }

        PxShape shape = PhysicsManager.GetPhysics().createShape(geometry, material, true, shapeFlags);
        body = PhysicsManager.GetPhysics().createRigidDynamic(tmpPose);
        shape.setSimulationFilterData(tmpFilterData);

        body.attachShape(shape);
        body.setMass(mass);

        shape.release();

        PhysicsManager.GetPhysicsScene().addActor(body);
    }

    private void ResetBody() {
        body.setGlobalPose(new PxTransform(PhysxUtil.ToPx3(gameObject.transform.position), PhysxUtil.SetEuler(gameObject.transform.rotation)));
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

    /**
     * Adds a force to an object
     * @param force Strength of force
     * @param forceMode The type of force that is used on the object
     */
    public void AddForce(Vector3 force, ForceMode forceMode) {
        int mode;
        switch (forceMode) {
            case Force:
                mode = PxForceModeEnum.eFORCE;
                break;
            case Impulse:
                mode = PxForceModeEnum.eIMPULSE;
                break;
            default:
                mode = PxForceModeEnum.eACCELERATION;
                break;
        }

        body.addForce(PhysxUtil.ToPx3(force), mode);
    }

    /**
     * Adds torque to an object
     * @param torque The strength of torque
     */
    public void AddTorque(Vector3 torque) {
        body.addTorque(PhysxUtil.ToPx3(torque));
    }

    /**
     * Sets the rigidbodies velocity
     * @param velocity The new velocity
     */
    public void SetVelocity(Vector3 velocity) {
        body.setLinearVelocity(new PxVec3(velocity.x, velocity.y, velocity.z));
    }

    /**
     * Sets the rigidbodies angular velocity
     * @param velocity The new angular velocity
     */
    public void SetAngularVelocity(Vector3 velocity) {
        body.setAngularVelocity(new PxVec3(velocity.x, velocity.y, velocity.z));
    }

    /**
     * Sets the colliders radius
     * @param radius The new collider radius
     */
    public void SetColliderRadius(float radius) {
        this.radius = radius;

        UpdateVariable();
    }

    /**
     * Sets the collider scale
     * @param colliderScale The new collider scale
     */
    public void SetColliderScale(Vector3 colliderScale) {
        this.colliderScale = colliderScale;

        UpdateVariable();
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
