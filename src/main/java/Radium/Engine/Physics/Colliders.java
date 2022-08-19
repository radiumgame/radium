package Radium.Engine.Physics;

import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.GameObject;
import org.lwjgl.system.MemoryStack;
import physx.common.PxBoundedData;
import physx.common.PxVec3;
import physx.cooking.PxCooking;
import physx.cooking.PxTriangleMeshDesc;
import physx.geomutils.PxGeometry;
import physx.geomutils.PxTriangleMesh;
import physx.geomutils.PxTriangleMeshFlagEnum;
import physx.geomutils.PxTriangleMeshGeometry;
import physx.support.Vector_PxU32;
import physx.support.Vector_PxVec3;

public class Colliders {

    protected Colliders() {}

    public static PxGeometry TriangleMeshCollider(GameObject gameObject, Mesh mesh) {
        PxCooking c = PhysicsManager.GetCooking();

        try (MemoryStack mem = MemoryStack.stackPush()) {
            Vector_PxVec3 pointVector = new Vector_PxVec3();
            Vector_PxU32 indexVector = new Vector_PxU32();
            for (int i = 0; i < mesh.GetVertices().length; i++) {
                Vector3 finalPosition = mesh.GetVertices()[i].GetPosition();
                finalPosition = Vector3.Multiply(finalPosition, gameObject.transform.WorldScale());
                pointVector.push_back(PhysxUtil.ToPx3(finalPosition));
            }
            for (int i = 0; i < mesh.GetIndices().length; i++) {
                indexVector.push_back(mesh.GetIndices()[i]);
            }

            PxBoundedData points = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
            points.setCount(pointVector.size());
            points.setStride(PxVec3.SIZEOF);
            points.setData(pointVector.data());

            PxBoundedData triangles = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
            triangles.setCount(indexVector.size() / 3);
            triangles.setStride(4 * 3);
            triangles.setData(indexVector.data());

            PxTriangleMeshDesc desc = PxTriangleMeshDesc.createAt(mem, MemoryStack::nmalloc);
            desc.setPoints(points);
            desc.setTriangles(triangles);

            PxTriangleMesh triangleMesh = c.createTriangleMesh(desc, PhysicsManager.GetPhysics().getPhysicsInsertionCallback());
            return new PxTriangleMeshGeometry(triangleMesh);
        }
    }

}
