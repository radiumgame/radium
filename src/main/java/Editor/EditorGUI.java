package Editor;

import Engine.Color;
import Engine.Math.Vector.Vector2;
import Engine.Math.Vector.Vector3;
import Engine.Util.NonInstantiatable;
import imgui.ImColor;
import imgui.ImGui;
import imgui.type.ImString;

public final class EditorGUI extends NonInstantiatable {

    public static void Text(String content) {
        ImGui.text(content);
    }

    public static void Text(String content, Color color) {
        ImGui.textColored(ImColor.floatToColor(color.r, color.g, color.b, color.a), content);
    }

    public static boolean Button(String content) {
        return ImGui.button(content);
    }

    public static int DragInt(String label, int displayValue) {
        int newInt = displayValue;

        int[] imInt = { displayValue };
        if (ImGui.dragInt(label, imInt)) {
            newInt = imInt[0];
        }

        return newInt;
    }

    public static float DragFloat(String label, float displayValue) {
        float newFloat = displayValue;

        float[] imFloat = { displayValue };
        if (ImGui.dragFloat(label, imFloat)) {
            newFloat = imFloat[0];
        }

        return newFloat;
    }

    public static boolean Checkbox(String label, boolean displayValue) {
        boolean newBoolean = displayValue;

        if (ImGui.checkbox(label, displayValue)) {
            newBoolean = !displayValue;
        }

        return newBoolean;
    }

    public static String InputString(String label, String displayValue) {
        String newString = displayValue;

        ImString outString = new ImString(displayValue, 256);
        if (ImGui.inputText(label, outString)) {

            newString = outString.get();
        }

        return newString;
    }

    public static Vector2 DragVector2(String label, Vector2 displayVector) {
        Vector2 newVector = displayVector;

        float[] imVec = { displayVector.x, displayVector.y };
        if (ImGui.dragFloat2(label, imVec)) {
            newVector.Set(imVec[0], imVec[1]);
        }

        return newVector;
    }

    public static Vector3 DragVector3(String label, Vector3 displayVector) {
        Vector3 newVector = displayVector;

        float[] imVec = { displayVector.x, displayVector.y, displayVector.z };
        if (ImGui.dragFloat3(label, imVec)) {
            newVector.Set(imVec[0], imVec[1], imVec[2]);
        }

        return newVector;
    }

    public static Color ColorField(String label, Color displayColor) {
        Color newColor = displayColor;

        float[] imColor = { displayColor.r, displayColor.g, displayColor.b, displayColor.a };
        if (ImGui.colorEdit4(label, imColor)) {
            newColor.Set(imColor[0], imColor[1], imColor[2], imColor[3]);
        }

        return newColor;
    }

}
