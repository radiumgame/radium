package Radium.Engine.Math;

import Radium.Engine.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMatrix4x4;

/**
 * Matrix functions
 */
public class Matrix4 {

    protected Matrix4() {}

    /**
     * Creates model matrix from transform
     * @param transform Transform
     * @param local Use local transform
     * @return Model matrix
     */
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

    /**
     * Creates model matrix from transform using world transform
     * @param transform Transform
     * @return Model matrix
     */
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

    /**
     * Create view matrix from position and rotation
     * @param camera Camera transform
     * @return View matrix
     */
    @Deprecated
    public static Matrix4f View(Transform camera) {
        Matrix4f viewMatrix = new Matrix4f().identity();
        viewMatrix.rotate(Mathf.Radians(camera.rotation.x), new Vector3f(1, 0, 0))
                .rotate(Mathf.Radians(camera.rotation.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-camera.position.x, -camera.position.y, -camera.position.z);

        return viewMatrix;
    }

    /**
     * Returns view matrix in world transform
     * @param camera Transform of camera
     * @param local Use local transforms
     * @return View matrix
     */
    public static Matrix4f View(Transform camera, boolean local) {
        if (local) {
            Matrix4f viewMatrix = new Matrix4f().identity();
            viewMatrix.rotate(Mathf.Radians(camera.WorldRotation().x), new Vector3f(1, 0, 0))
                    .rotate(Mathf.Radians(camera.WorldRotation().y), new Vector3f(0, 1, 0))
                    .rotate(Mathf.Radians(camera.WorldRotation().z), new Vector3f(0, 0, 1));
            viewMatrix.translate(-camera.WorldPosition().x, -camera.WorldPosition().y, -camera.WorldPosition().z);

            return viewMatrix;
        } else {
            return View(camera);
        }
    }

    public static Matrix4f FromAssimp(AIMatrix4x4 matrix) {
        return new Matrix4f(matrix.a1(), matrix.a2(), matrix.a3(), matrix.a4(),
                matrix.b1(), matrix.b2(), matrix.b3(), matrix.b4(),
                matrix.c1(), matrix.c2(), matrix.c3(), matrix.c4(),
                matrix.d1(), matrix.d2(), matrix.d3(), matrix.d4());
    }

}
