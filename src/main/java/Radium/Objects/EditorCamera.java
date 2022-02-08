package Radium.Objects;

import RadiumEditor.Viewport;
import Radium.*;
import Radium.Input.Input;
import Radium.Math.*;
import Radium.Math.Vector.Vector3;
import org.joml.Matrix4f;

/**
 * Camera that editor uses when in edit mode
 */
public class EditorCamera {

    /**
     * Transform of the editor camera
     */
    public Transform transform = new Transform();
    private Matrix4f projection;

    private float oldMouseX = 0, newMouseX = 0, oldMouseY = 0, newMouseY = 0;

    private Vector3 focusOffset = new Vector3(1, 1, 1);

    private Matrix4f view;

    /**
     * Create empty editor camera and calculate projection matrix
     */
    public EditorCamera() {
        CalculateProjection();
    }

    /**
     * Update the movement and view matrices
     */
    public void Update() {
        Movement();

        CalculateView();
    }

    /**
     * Move and focus camera on a transform
     * @param gameObject Target
     */
    public void Focus(GameObject gameObject) {
        transform.position = Vector3.Add(gameObject.transform.position, focusOffset);
        transform.rotation = QuaternionUtility.LookAt(transform, gameObject.transform.position);
    }

    private Vector3 zoomFactor = new Vector3(5, 5, 5);
    private void Movement() {
        if (Application.Playing || !Viewport.ViewportHovered) return;

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

    /**
     * Calculates the projection matrices of camera
     */
    public void CalculateProjection() {
        float aspect = (float) Window.width / (float)Window.height;
        projection = new Matrix4f().perspective(Mathf.Radians(70f), aspect, 0.1f, 100f);
    }

    private void CalculateView() {
        view = Matrix4.View(transform);
    }

    /**
     * @return Camera projection matrices
     */
    public Matrix4f GetProjection() {
        return projection;
    }

    /**
     * @return Camera view matrices
     */
    public Matrix4f GetView() {
        return view;
    }
}
