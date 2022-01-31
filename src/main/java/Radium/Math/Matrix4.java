package Radium.Math;

import Radium.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Matrix4 {

    protected Matrix4() {}

    public static Matrix4f Transform(Transform transform, boolean local) {
        if (local) {
            return Transform(transform);
        } else {
            Matrix4f transformMatrix = new Matrix4f().identity();
            transformMatrix.translate(transform.position.x, transform.position.y, transform.position.z);
            transformMatrix.rotateX(Mathf.Radians(transform.rotation.x));
            transformMatrix.rotateY(Mathf.Radians(transform.rotation.y));
            transformMatrix.rotateZ(Mathf.Radians(transform.rotation.z));
            transformMatrix.scale(transform.scale.x, transform.scale.y, transform.scale.z);

            return transformMatrix;
        }
    }

    public static Matrix4f Transform(Transform transform) {
        Vector3 worldPosition = transform.WorldPosition();
        Vector3 worldRotation = transform.WorldRotation();
        Vector3 worldScale = transform.WorldScale();

        Matrix4f transformMatrix = new Matrix4f().identity();
        transformMatrix.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformMatrix.rotateX(Mathf.Radians(worldRotation.x));
        transformMatrix.rotateY(Mathf.Radians(worldRotation.y));
        transformMatrix.rotateZ(Mathf.Radians(worldRotation.z));
        transformMatrix.scale(worldScale.x, worldScale.y, worldScale.z);

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

    public static Matrix4f View(Transform camera, boolean local) {
        if (local) {
            Matrix4f viewMatrix = new Matrix4f().identity();
            viewMatrix.rotate(Mathf.Radians(camera.WorldRotation().x), new Vector3f(1, 0, 0))
                    .rotate(Mathf.Radians(camera.WorldRotation().y), new Vector3f(0, 1, 0));
            viewMatrix.translate(-camera.WorldPosition().x, -camera.WorldPosition().y, -camera.WorldPosition().z);

            return viewMatrix;
        } else {
            return View(camera);
        }
    }

}
