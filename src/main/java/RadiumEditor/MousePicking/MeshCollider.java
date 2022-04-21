package RadiumEditor.MousePicking;

import Radium.Graphics.Mesh;
import Radium.Math.Random;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.Physics.ColliderType;
import Radium.Physics.PhysicsManager;
import Radium.Physics.PhysxUtil;
import RadiumEditor.Console;
import physx.NativeObject;
import physx.PxTopLevelFunctions;
import physx.character.PxExtendedVec3;
import physx.common.*;
import physx.cooking.PxConvexFlagEnum;
import physx.cooking.PxConvexFlags;
import physx.cooking.PxConvexMeshCookingTypeEnum;
import physx.cooking.PxConvexMeshDesc;
import physx.extensions.PxDefaultMemoryOutputStream;
import physx.geomutils.*;
import physx.physics.*;
import physx.support.Vector_PxVec3;

import java.util.UUID;

public class MeshCollider {

    private GameObject object;
    private Mesh mesh;

    private PxRigidStatic body;
    private PxShape shape;

    public MeshCollider(GameObject object, Mesh mesh) {
        this.object = object;
        this.mesh = mesh;

        CreateCollider();
    }

    public void SetTransform() {
        body.setGlobalPose(new PxTransform(PhysxUtil.ToPx3(object.transform.WorldPosition()), PhysxUtil.SetEuler(object.transform.WorldRotation())));

        body.detachShape(shape);
        PxMaterial material = MousePickingCollision.GetPhysics().createMaterial(0.5f, 0.5f, 0.5f);
        PxShapeFlags shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE));
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);
        Vector3 scale = object.transform.WorldScale();
        PxGeometry geometry = new PxBoxGeometry(scale.x, scale.y, scale.z);
        shape = MousePickingCollision.GetPhysics().createShape(geometry, material, true, shapeFlags);
        shape.setSimulationFilterData(tmpFilterData);
        body.attachShape(shape);
    }

    private void CreateCollider() {
        PxMaterial material = MousePickingCollision.GetPhysics().createMaterial(0.5f, 0.5f, 0.5f);
        PxShapeFlags shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE));
        PxTransform tmpPose = new PxTransform(PhysxUtil.ToPx3(object.transform.WorldPosition()), PhysxUtil.SetEuler(object.transform.WorldRotation()));
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        Vector3 scale = object.transform.WorldScale();
        PxGeometry geometry = new PxBoxGeometry(scale.x, scale.y, scale.z);
        shape = MousePickingCollision.GetPhysics().createShape(geometry, material, true, shapeFlags);
        body = MousePickingCollision.GetPhysics().createRigidStatic(tmpPose);
        shape.setSimulationFilterData(tmpFilterData);

        body.attachShape(shape);
        body.setName(object.id);

        shape.release();

        MousePickingCollision.GetScene().addActor(body);
    }

}
