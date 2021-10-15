package Engine.Objects;

import Engine.*;
import Engine.Input.Input;
import Engine.Math.Axis;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class EditorCamera {

    public Transform transform = new Transform();
    public Matrix4f projection;

    private float oldMouseX = 0, newMouseX = 0, oldMouseY = 0, newMouseY = 0;

    public void Update() {
        CalculateProjection();
        Movement();
    }

    private Vector3 zoomFactor = new Vector3(5, 5, 5);
    private void Movement() {
        if (Application.Playing) return;

        newMouseX = (float) Input.GetMouseX();
        newMouseY = (float) Input.GetMouseY();

        if (Input.GetScrollY() != 0) {
            if (Input.GetScrollY() > 0) {
                transform.position = Vector3.Add(transform.position, Vector3.Divide(transform.Forward(), zoomFactor));
            } else {
                transform.position = Vector3.Add(transform.position, Vector3.Divide(transform.Back(), zoomFactor));
            }

            Input.ResetScroll();
        }

        if (Input.GetMouseButton(1)) {
            float dx = newMouseX - oldMouseX;
            float dy = newMouseY - oldMouseY;

            transform.rotation = Vector3.Add(transform.rotation, new Vector3(-dy * 0.125f, -dx * 0.125f, 0));
            transform.rotation.Clamp(Axis.X, -80, 80);
        }

        oldMouseX = newMouseX;
        oldMouseY = newMouseY;
    }

    private void CalculateProjection() {
        float aspect = (float) Window.width / (float)Window.height;
        projection = new Matrix4f().perspective((float)Math.toRadians(70), aspect, 0.1f, Variables.DefaultCamera.far);
    }
}
