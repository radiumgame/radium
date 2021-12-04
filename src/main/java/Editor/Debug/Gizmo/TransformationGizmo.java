package Editor.Debug.Gizmo;

import Editor.Console;
import Editor.SceneHierarchy;
import Engine.Input.Input;
import Engine.Input.Keys;
import Engine.Math.Matrix4;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector3;
import Engine.Objects.GameObject;
import Engine.Util.NonInstantiatable;
import Engine.Variables;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiKey;
import org.joml.Matrix4f;

public final class TransformationGizmo extends NonInstantiatable {

    private static float[] cameraView = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f
    };

    private static int operation = Operation.TRANSLATE;

    public static void Update(ImVec2 size) {
        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(ImGui.getWindowPosX() + ((ImGui.getWindowWidth() - size.x) / 2), ImGui.getWindowPosY() + ((ImGui.getWindowHeight() - size.y) / 2), size.x, size.y);

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
            }
        } else if (Input.GetKey(Keys.Y)) {
            switch (operation) {
                case Operation.TRANSLATE -> operation = Operation.TRANSLATE_Y;
                case Operation.ROTATE -> operation = Operation.ROTATE_Y;
                case Operation.SCALE -> operation = Operation.SCALE_Y;
            }
        } else if (Input.GetKey(Keys.Z)) {
            switch (operation) {
                case Operation.TRANSLATE -> operation = Operation.TRANSLATE_Z;
                case Operation.ROTATE -> operation = Operation.ROTATE_Z;
                case Operation.SCALE -> operation = Operation.SCALE_Z;
            }
        } else {
            switch (operation) {
                case Operation.TRANSLATE_X -> operation = Operation.TRANSLATE;
                case Operation.TRANSLATE_Y -> operation = Operation.TRANSLATE;
                case Operation.TRANSLATE_Z -> operation = Operation.TRANSLATE;

                case Operation.ROTATE_X -> operation = Operation.ROTATE;
                case Operation.ROTATE_Y -> operation = Operation.ROTATE;
                case Operation.ROTATE_Z -> operation = Operation.ROTATE;

                case Operation.SCALE_X -> operation = Operation.SCALE;
                case Operation.SCALE_Y -> operation = Operation.SCALE;
                case Operation.SCALE_Z -> operation = Operation.SCALE;
            }
        }

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
            transform.position = pos;
            transform.rotation = rot;
            transform.scale = sca;
        }
    }

    private static void CalculateView() {
        Matrix4f view = Variables.EditorCamera.GetView();
        view.get(cameraView);
    }

    private static float[] Model() {
        GameObject current = SceneHierarchy.current;

        float[] model = new float[16];
        float[] position = Array(current.transform.position);
        float[] rotation = Array(current.transform.rotation);
        float[] scale = Array(current.transform.scale);
        ImGuizmo.recomposeMatrixFromComponents(model, position, rotation, scale);
        //Matrix4f transform = Matrix4.Transform(current.transform);
        //transform.get(model);

        return model;
    }

    private static float[] Array(Vector3 vector) {
        return new float[] { vector.x, vector.y, vector.z };
    }

    private static Vector3 Vec3(float[] arr) {
        return new Vector3(arr[0], arr[1], arr[2]);
    }

}
