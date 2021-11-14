package Engine.Components.Physics;

import Editor.Console;
import Engine.Component;
import Engine.Debug.Gizmo.ColliderGizmo;
import Engine.Graphics.Texture;
import Engine.Math.Vector.Vector2;
import Engine.Math.Vector.Vector3;
import Engine.PerformanceImpact;
import Engine.Physics.ColliderType;
import Engine.Physics.ForceMode;
import Engine.Physics.PhysicsManager;
import Engine.Physics.PhysxUtil;
import imgui.ImGui;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geomutils.*;
import physx.physics.*;

public class Rigidbody extends Component {

    public float mass = 1f;
    public boolean applyGravity = true;
    public boolean lockRotation = false;

    public ColliderType collider = ColliderType.Box;
    public boolean showCollider = true;

    private transient PxRigidDynamic body;

    private float radius = 0.5f;
    private Vector3 colliderScale = Vector3.One;

    private transient ColliderGizmo gizmo;

    public Rigidbody() {
        description = "A body that handles collisions and physics";
        impact = PerformanceImpact.Medium;
        icon = new Texture("EngineAssets/Editor/Icons/rigidbody.png").textureID;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        if (!applyGravity) {
            body.setLinearVelocity(new PxVec3(0, 0, 0));
        } if (lockRotation) {
            PxTransform pose = body.getGlobalPose();
            pose.setQ(PhysxUtil.SetEuler(gameObject.transform.rotation));
            body.setGlobalPose(pose);
        }

        gameObject.transform.position = PhysxUtil.FromPx3(body.getGlobalPose().getP());
        gameObject.transform.rotation = PhysxUtil.GetEuler(body.getGlobalPose().getQ());
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
        CreateBody();
        gizmo.UpdateCollider();

        body.setMass(mass);
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
        PxTransform tmpPose = new PxTransform(PhysxUtil.ToPx3(gameObject.transform.position), PhysxUtil.SetEuler(gameObject.transform.rotation));
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        PxGeometry geometry = null;
        Vector3 scale = gameObject.transform.scale;
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

    public void AddForce(Vector3 force) {
        body.addForce(PhysxUtil.ToPx3(force));
    }

    public void AddForce(Vector3 force, ForceMode forceMode) {
        int mode = PxForceModeEnum.eFORCE;
        switch (forceMode) {
            case Acceleration:
                mode = PxForceModeEnum.eACCELERATION;
                break;
            case Force:
                mode = PxForceModeEnum.eFORCE;
                break;
            case Impulse:
                mode = PxForceModeEnum.eIMPULSE;
                break;
        }

        body.addForce(PhysxUtil.ToPx3(force), mode);
    }

    public void AddTorque(Vector3 torque) {
        body.addTorque(PhysxUtil.ToPx3(torque));
    }

    public void SetVelocity(Vector3 velocity) {
        body.setLinearVelocity(new PxVec3(velocity.x, velocity.y, velocity.z));
    }

    public void SetAngularVelocity(Vector3 velocity) {
        body.setAngularVelocity(new PxVec3(velocity.x, velocity.y, velocity.z));
    }

    public void SetColliderRadius(float radius) {
        this.radius = radius;

        UpdateVariable();
    }

    public void SetColliderScale(Vector3 colliderScale) {
        this.colliderScale = colliderScale;

        UpdateVariable();
    }

    public float GetColliderRadius() {
        return radius;
    }

    public Vector3 GetColliderScale() {
        return colliderScale;
    }
}
