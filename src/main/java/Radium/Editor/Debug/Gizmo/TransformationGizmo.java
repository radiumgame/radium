package Radium.Editor.Debug.Gizmo;

import Radium.Engine.Components.Physics.Rigidbody;
import Radium.Editor.SceneHierarchy;
import Radium.Engine.Input.Input;
import Radium.Engine.Input.Keys;
import Radium.Engine.Math.Transform;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Variables;
import Radium.Editor.Viewport;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import org.joml.Matrix4f;

/**
 * Draggable gizmos for adjusting a transform
 */
public class TransformationGizmo {

    private static final float[] cameraView = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f
    };

    /**
     * Transform operation
     */
    public static int operation = Operation.TRANSLATE;

    protected TransformationGizmo() {}

    /**
     * Sets the transform operation
     * @param op Transform operation
     */
    public static void SetOperation(int op) {
        operation = op;
    }

    /**
     * Renders and updates input
     * @param size Viewport size
     */
    public static boolean Update(ImVec2 size) {
        SetupImGuizmo(size);
        CheckOperations();

        CalculateView();
        float[] cameraProjection = new float[16];
        Variables.EditorCamera.GetProjection().get(cameraProjection);
        float[] model = Model();

        ImGuizmo.manipulate(cameraView, cameraProjection, model, operation, Mode.LOCAL);
        if (ImGuizmo.isUsing()) {
            float[] position = new float[3];
            float[] rotation = new float[3];
            float[] scale = new float[3];
            ImGuizmo.decomposeMatrixToComponents(model, position, rotation, scale);
            Vector3 pos = Vec3(position);
            Vector3 rot = Vec3(rotation);
            Vector3 sca = Vec3(scale);

            Transform transform = SceneHierarchy.current.transform;
            transform.SetPositionFromWorld(pos);
            transform.localRotation = rot;
            transform.localScale = sca;

            Rigidbody rb = SceneHierarchy.current.GetComponent(Rigidbody.class);
            if (rb != null) {
                rb.UpdateBodyTransform();
            }

            return true;
        }

        return false;
    }

    private static void SetupImGuizmo(ImVec2 size) {
        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();

        Vector2 imagePosition = Viewport.imagePosition;
        Vector2 imageSize = Viewport.imageSize;
        Vector2 viewportPosition = Viewport.position;
        ImGuizmo.setRect(viewportPosition.x + imagePosition.x, viewportPosition.y + imagePosition.y, imageSize.x, imageSize.y);
    }

    private static void CheckOperations() {
        if (!Viewport.ViewportFocused) return;

        if (Input.GetKey(Keys.T)) {
            operation = Operation.TRANSLATE;
        } else if (Input.GetKey(Keys.R)) {
            operation = Operation.ROTATE;
        } else if (Input.GetKey(Keys.S)) {
            operation = Operation.SCALE;
        }

        if (Input.GetKey(Keys.X)) {
            switch (operation) {
                case Operation.TRANSLATE -> operation = Operation.TRANSLATE_X;
                case Operation.ROTATE -> operation = Operation.ROTATE_X;
                case Operation.SCALE -> operation = Operation.SCALE_X;
                default -> operation = Operation.TRANSLATE;
            }
        } else if (Input.GetKey(Keys.Y)) {
            switch (operation) {
                case Operation.TRANSLATE -> operation = Operation.TRANSLATE_Y;
                case Operation.ROTATE -> operation = Operation.ROTATE_Y;
                case Operation.SCALE -> operation = Operation.SCALE_Y;
                default -> operation = Operation.TRANSLATE;
            }
        } else if (Input.GetKey(Keys.Z)) {
            switch (operation) {
                case Operation.TRANSLATE -> operation = Operation.TRANSLATE_Z;
                case Operation.ROTATE -> operation = Operation.ROTATE_Z;
                case Operation.SCALE -> operation = Operation.SCALE_Z;
                default -> operation = Operation.TRANSLATE;
            }
        } else {
            switch (operation) {
                case Operation.TRANSLATE_X, Operation.TRANSLATE_Y, Operation.TRANSLATE_Z -> operation = Operation.TRANSLATE;
                case Operation.ROTATE_X, Operation.ROTATE_Y, Operation.ROTATE_Z -> operation = Operation.ROTATE;
                case Operation.SCALE_X, Operation.SCALE_Y, Operation.SCALE_Z -> operation = Operation.SCALE;
            }
        }
    }

    private static void CalculateView() {
        Matrix4f view = Variables.EditorCamera.GetView();
        view.get(cameraView);
    }

    private static float[] Model() {
        GameObject current = SceneHierarchy.current;

        float[] model = new float[16];
        float[] position = Array(current.transform.WorldPosition());
        float[] rotation = Array(current.transform.localRotation);
        float[] scale = Array(current.transform.localScale);
        ImGuizmo.recomposeMatrixFromComponents(model, position, rotation, scale);

        return model;
    }

    private static float[] EmptyModel() {
        float[] model = new float[16];
        float[] position = new float[] { 0, 0, 0 };
        float[] rotation = new float[] { 0, 0, 0 };
        float[] scale = new float[] { 1, 1, 1 };
        ImGuizmo.recomposeMatrixFromComponents(model, position, rotation, scale);

        return model;
    }

    private static float[] Array(Vector3 vector) {
        return new float[] { vector.x, vector.y, vector.z };
    }

    private static Vector3 Vec3(float[] arr) {
        return new Vector3(arr[0], arr[1], arr[2]);
    }

}
