package Engine.Math;

import Engine.Util.NonInstantiatable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Matrix4 extends NonInstantiatable {

    public static Matrix4f Transform(Transform transform) {
        Matrix4f transformMatrix = new Matrix4f().identity();
        transformMatrix.translate(transform.position.x, transform.position.y, transform.position.z);
        transformMatrix.rotateX((float)Math.toRadians(transform.rotation.x));
        transformMatrix.rotateY((float)Math.toRadians(transform.rotation.y));
        transformMatrix.rotateZ((float)Math.toRadians(transform.rotation.z));
        transformMatrix.scale(transform.scale.x, transform.scale.y, transform.scale.z);

        return transformMatrix;
    }

    public static Matrix4f View(Transform camera) {
        Matrix4f viewMatrix = new Matrix4f().identity();
        viewMatrix.rotate((float)Math.toRadians(camera.rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(camera.rotation.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-camera.position.x, -camera.position.y, -camera.position.z);

        return viewMatrix;
    }

}
