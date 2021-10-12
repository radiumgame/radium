package Engine.Objects;

import Editor.Console;
import Engine.*;
import Engine.Math.Axis;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector2;
import Engine.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Vector;

public class EditorCamera {

    public Transform transform = new Transform();
    public Matrix4f projection;

    private float oldMouseX = 0, newMouseX = 0, oldMouseY = 0, newMouseY = 0;
    private float zoomSpeed = 1 / 20f;

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

    private Vector3 Lerp(Vector3 vector, Vector3 other, float time) {
        Vector3f me = new Vector3f(vector.x, vector.y, other.z);
        Vector3f otherj = new Vector3f(other.x, other.y, other.z);

        Vector3f lerped = me.lerp(otherj, time);
        return new Vector3(lerped.x, lerped.y, lerped.z);
    }

}
