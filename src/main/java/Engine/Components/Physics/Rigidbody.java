package Engine.Components.Physics;

import Engine.Component;
import Engine.Graphics.Texture;
import Engine.Math.Vector.Vector3;
import Engine.PerformanceImpact;
import Engine.Physics.ForceMode;
import Engine.Physics.PhysicsManager;
import Engine.Physics.PhysxUtil;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geomutils.*;
import physx.physics.*;

public class Rigidbody extends Component {

    public float mass = 1f;
    public boolean applyGravity = true;
    public boolean lockRotation = false;

    private transient PxRigidDynamic body;
    private Vector3 colliderScale = Vector3.One;

    public Rigidbody() {
        description = "A body that handles collisions and physics";
        impact = PerformanceImpact.Medium;
        icon = new Texture("EngineAssets/Editor/Icons/rigidbody.png").textureID;
    }

    public Rigidbody(Vector3 colliderScale) {
        description = "A body that handles collisions and physics";
        impact = PerformanceImpact.Medium;
        icon = new Texture("EngineAssets/Editor/Icons/rigidbody.png").textureID;

        this.colliderScale = colliderScale;
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
    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void OnVariableUpdate() {
        body.setMass(mass);
    }

    @Override
    public void GUIRender() {

    }

    public PxRigidDynamic GetBody() {
        return body;
    }

    private void CreateBody() {
        PxMaterial material = PhysicsManager.GetPhysics().createMaterial(0.5f, 0.5f, 0.5f);
        PxShapeFlags shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE));
        PxTransform tmpPose = new PxTransform(PhysxUtil.ToPx3(gameObject.transform.position), PhysxUtil.SetEuler(gameObject.transform.rotation));
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        PxBoxGeometry boxGeometry = new PxBoxGeometry(colliderScale.x / 2, colliderScale.y / 2, colliderScale.z / 2);
        PxShape shape = PhysicsManager.GetPhysics().createShape(boxGeometry, material, true, shapeFlags);
        body = PhysicsManager.GetPhysics().createRigidDynamic(tmpPose);
        shape.setSimulationFilterData(tmpFilterData);

        body.attachShape(shape);
        body.setMass(mass);

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

}
