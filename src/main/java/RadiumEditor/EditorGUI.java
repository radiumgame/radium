package RadiumEditor;

import Radium.Color;
import Radium.Graphics.Texture;
import Radium.Math.Random;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.System.FileExplorer;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import org.jetbrains.annotations.NotNull;

public class EditorGUI {

    protected EditorGUI() {}

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

    public static int SliderInt(String label, int displayValue, int min, int max) {
        int newInt = displayValue;

        int[] imInt = { displayValue };
        if (ImGui.sliderInt(label, imInt, min, max)) {
            newInt = imInt[0];
        }

        return newInt;
    }

    public static float SliderFloat(String label, float displayValue, float min, float max) {
        float newFloat = displayValue;

        float[] imFloat = { displayValue };
        if (ImGui.sliderFloat(label, imFloat, min, max)) {
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

    public static Texture TextureField(@NotNull Texture displayTexture) {
        Texture newTexture = null;

        ImGui.image(displayTexture.textureID, 90, 90);

        ImGui.sameLine();
        if (ImGui.button("Choose ##Texture" + displayTexture.textureID)) {
            String path = FileExplorer.Choose("png,jpg,bmp;");

            if (path != null) {
                newTexture = new Texture(path);
            }
        }
        ImGui.sameLine();
        if (ImGui.treeNodeEx((displayTexture == null) ? "(Texture) None" : "(Texture) " + displayTexture.filepath, ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.Leaf)) {
            ImGui.treePop();
        }

        return newTexture;
    }

}
