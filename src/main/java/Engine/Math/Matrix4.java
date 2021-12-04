package Engine.Math;

import Engine.Math.Vector.Vector3;
import Engine.Util.NonInstantiatable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class Matrix4 extends NonInstantiatable {

    public static Matrix4f Transform(Transform transform) {
        Matrix4f transformMatrix = new Matrix4f().identity();
        transformMatrix.translate(transform.position.x, transform.position.y, transform.position.z);
        transformMatrix.rotateX(Mathf.Radians(transform.rotation.x));
        transformMatrix.rotateY(Mathf.Radians(transform.rotation.y));
        transformMatrix.rotateZ(Mathf.Radians(transform.rotation.z));
        transformMatrix.scale(transform.scale.x, transform.scale.y, transform.scale.z);

        return transformMatrix;
    }

    public static Matrix4f ModelView(Transform transform, Matrix4f view) {
        Matrix4f model = new Matrix4f();
        model.translate(transform.position.x, transform.position.x, transform.position.x);
        model.m00(view.m00());
        model.m01(view.m01());
        model.m02(view.m02());
        model.m10(view.m10());
        model.m11(view.m11());
        model.m12(view.m12());
        model.m20(view.m20());
        model.m21(view.m21());
        model.m22(view.m22());
        model.rotate(Mathf.Radians(transform.rotation.z), new Vector3f(0, 0, 1));
        model.scale(transform.scale.x, transform.scale.y, transform.scale.z);

        Matrix4f modelView = view.mul(model);
        return modelView;
    }

    public static Matrix4f View(Transform camera) {
        Matrix4f viewMatrix = new Matrix4f().identity();
        viewMatrix.rotate(Mathf.Radians(camera.rotation.x), new Vector3f(1, 0, 0))
                .rotate(Mathf.Radians(camera.rotation.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-camera.position.x, -camera.position.y, -camera.position.z);

        return viewMatrix;
    }

}
