package Radium.Components.Physics;

import Radium.Component;
import Radium.Math.Random;
import RadiumEditor.Console;
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

import java.util.UUID;

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
    public boolean isStatic = false;
    public boolean isKinematic = false;

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

        if (isStatic) {
            PxTransform tmpPose = new PxTransform(PhysxUtil.ToPx3(gameObject.transform.localPosition), PhysxUtil.SetEuler(gameObject.transform.localRotation));
            body.setGlobalPose(tmpPose);
        }
        body.setRigidBodyFlag(PxRigidBodyFlagEnum.eKINEMATIC, isKinematic);

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
        PhysicsManager.GetPhysicsScene().removeActor(body);
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
            geometry = new PxBoxGeometry(colliderScale.x * scale.x, colliderScale.y * scale.y, colliderScale.z * scale.z);
        } else if (collider == ColliderType.Sphere) {
            geometry = new PxSphereGeometry(radius * 2);
        }

        PxShape shape = PhysicsManager.GetPhysics().createShape(geometry, material, true, shapeFlags);
        body = PhysicsManager.GetPhysics().createRigidDynamic(tmpPose);
        shape.setSimulationFilterData(tmpFilterData);

        body.attachShape(shape);
        body.setMass(mass);
        body.setName(gameObject.id);

        body.setLinearDamping(drag);
        body.setAngularDamping(angularDrag);

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
