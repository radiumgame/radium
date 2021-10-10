package Engine.Objects;

import Editor.Console;
import Engine.*;
import Engine.Math.Axis;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector2;
import Engine.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.Vector;

public class EditorCamera {

    public Transform transform = new Transform();
    public Matrix4f projection;

    private float oldMouseX = 0, newMouseX = 0, oldMouseY = 0, newMouseY = 0;
    private float zoomSpeed = 2.5f;

    public void Update() {
        CalculateProjection();
        Movement();
    }

    private void Movement() {
        if (Application.Playing) return;

        newMouseX = (float) Input.GetMouseX();
        newMouseY = (float) Input.GetMouseY();

        if (Input.GetScrollY() != 0) {
            if (Input.GetScrollY() > 0) {
                transform.position = Vector3.Add(transform.position, Vector3.Multiply(transform.Forward(), new Vector3(Time.deltaTime * zoomSpeed, Time.deltaTime * zoomSpeed, Time.deltaTime * zoomSpeed)));
            } else {
                transform.position = Vector3.Add(transform.position, Vector3.Multiply(transform.Back(), new Vector3(Time.deltaTime * zoomSpeed, Time.deltaTime * zoomSpeed, Time.deltaTime * zoomSpeed)));
            }
        }

        if (Input.GetMouseButton(1)) {
            float dx = newMouseX - oldMouseX;
            float dy = newMouseY - oldMouseY;

            transform.rotation = Vector3.Add(transform.rotation, new Vector3(-dy * 0.125f, -dx * 0.125f, 0));
            transform.rotation.Clamp(Axis.X, -80, 80);
        }

        oldMouseX = newMouseX;
        oldMouseY = newMouseY;

        Input.ResetScroll();
    }

    private void CalculateProjection() {
        float aspect = (float) Window.width / (float)Window.height;
        projection = new Matrix4f().perspective((float)Math.toRadians(70), aspect, 0.1f, Variables.DefaultCamera.far);
    }

    public Vector2 Lerp(Vector2 vector, Vector2 other, float time) {
        Vector2f me = new Vector2f(vector.x, vector.y);
        Vector2f otherj = new Vector2f(other.x, other.y);

        Vector2f lerped = me.lerp(otherj, time);
        return new Vector2(lerped.x, lerped.y);
    }

}
