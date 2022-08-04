package Radium.Engine.FrustumCulling;

import Radium.Engine.Application;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Variables;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

public class FrustumFilter {

    private final static Matrix4f prjViewMatrix = new Matrix4f();
    private static FrustumIntersection frustumInt;

    protected FrustumFilter() {}

    public static void Initialize() {
        frustumInt = new FrustumIntersection();
    }

    public static void UpdateFrustum() {
        Matrix4f projMatrix = Variables.EditorCamera.GetProjection();
        Matrix4f viewMatrix = Variables.EditorCamera.GetView();
        if (Application.Playing && Variables.DefaultCamera != null) {
            projMatrix = Variables.DefaultCamera.GetProjection();
            viewMatrix = Variables.DefaultCamera.GetView();
        }

        prjViewMatrix.set(projMatrix);
        prjViewMatrix.mul(viewMatrix);
        frustumInt.set(prjViewMatrix);
    }

    public static boolean InsideFrustumSphere(Vector3 position, float boundingRadius) {
        return frustumInt.testSphere(position.x, position.y, position.z, boundingRadius);
    }

    public static boolean InsideFrustumAABB(AABB aabb) {
        return frustumInt.testAab(aabb.min.x, aabb.min.y, aabb.min.z, aabb.max.x, aabb.max.y, aabb.max.z);
    }

}
