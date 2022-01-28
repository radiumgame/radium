package Radium.Graphics.CubeMap;

import Radium.Math.Mathf;
import Radium.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CubeMapCamera {

    private float AspectRatio = 16f / 9f;
    private float fov = 90;
    private float nearPlane = 0.1f, farPlane = 100f;
    private Vector3 position = Vector3.Zero;
    private float pitch = 0, yaw = 0;

    private Matrix4f projection;
    private Matrix4f view;
    private Matrix4f projectionView;

    public CubeMapCamera() {
        projection = new Matrix4f().identity();
        view = new Matrix4f().identity();
        projectionView = new Matrix4f().identity();
    }

    public void SwitchFace(int index) {
        switch (index) {
            case 0:
                pitch = 0;
                yaw = 90;
                break;
            case 1:
                pitch = 0;
                yaw = -90;
                break;
            case 2:
                pitch = -90;
                yaw = 180;
                break;
            case 3:
                pitch = 90;
                yaw = 180;
                break;
            case 4:
                pitch = 0;
                yaw = 180;
                break;
            case 5:
                pitch = 0;
                yaw = 0;
                break;
            default:
                pitch = 0;
                yaw = 90;
                break;
        }
    }

    private void Projection() {
        float yScale = (1f / Mathf.Tangent(Mathf.Radians(fov / 2f)));
        float xScale = yScale / AspectRatio;
        float frustumLength = farPlane - nearPlane;

        projection.m00(xScale);
        projection.m11(yScale);
        projection.m22(-((farPlane + nearPlane) / frustumLength));
        projection.m23(-1);
        projection.m32(-((2 * nearPlane * farPlane) / frustumLength));
        projection.m33(0);
    }

    private void View() {
        view = view.identity();
        view.rotate(Mathf.Radians(180), new Vector3f(0, 0, 1));
        view.rotate(Mathf.Radians(pitch), new Vector3f(1, 0, 0));
        view.rotate(Mathf.Radians(yaw), new Vector3f(0, 1, 0));
        Vector3f negativePosition = new Vector3f(-position.x, -position.y, -position.z);
        view.translate(negativePosition);

        projectionView = new Matrix4f(projection).mul(view);
    }

}
