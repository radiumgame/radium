package RadiumEditor;

import Radium.Color;
import Radium.Graphics.Texture;
import Radium.Math.Random;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.System.FileExplorer;
import Radium.Util.EnumUtility;
import Radium.Util.FileUtility;
import imgui.ImColor;
import imgui.ImGui;
import java.io.File;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.jetbrains.annotations.NotNull;

/**
 * Useful methods for showing GUI items such as input and sliders
 */
public class EditorGUI {

    protected EditorGUI() {}

    /**
     * Renders text
     * @param content Text content
     */
    public static void Text(String content) {
        ImGui.text(content);
    }

    /**
     * Renders colored text
     * @param content Text content
     * @param color Text color
     */
    public static void Text(String content, Color color) {
        ImGui.textColored(ImColor.floatToColor(color.r, color.g, color.b, color.a), content);
    }

    /**
     * Renders buffer
     * @param content Button text content
     * @return Is button pressed
     */
    public static boolean Button(String content) {
        return ImGui.button(content);
    }

    /**
     * Draggable int, no min or max bounds
     * @param label Text label
     * @param displayValue Value to show on slider
     * @return Drag value
     */
    public static int DragInt(String label, int displayValue) {
        int[] imInt = { displayValue };
        if (ImGui.dragInt(label, imInt)) {
            return imInt[0];
        }

        return displayValue;
    }

    /**
     * Draggable int, no min or max bounds
     * @param label Text label
     * @param displayValue Value to show on slider
     * @param width Width of drag int
     * @return Drag value
     */
    public static int DragInt(String label, int displayValue, float width) {
        int[] imInt = { displayValue };
        ImGui.setNextItemWidth(width);
        if (ImGui.dragInt(label, imInt)) {
            return imInt[0];
        }

        return displayValue;
    }

    /**
     * Draggable float, no min or max bounds
     * @param label Text label
     * @param displayValue Value to show on slider
     * @return Drag value
     */
    public static float DragFloat(String label, float displayValue) {
        float newFloat = displayValue;

        float[] imFloat = { displayValue };
        if (ImGui.dragFloat(label, imFloat)) {
            newFloat = imFloat[0];
        }

        return newFloat;
    }

    /**
     * Slider int, with min and max bounds
     * @param label Text label
     * @param displayValue Value to show on slider
     * @param min Minimum value for slider
     * @param max Maximum value for slider
     * @return Slider value
     */
    public static int SliderInt(String label, int displayValue, int min, int max) {
        int newInt = displayValue;

        int[] imInt = { displayValue };
        if (ImGui.sliderInt(label, imInt, min, max)) {
            newInt = imInt[0];
        }

        return newInt;
    }

    /**
     * Slider float, with min and max bounds
     * @param label Text label
     * @param displayValue Value to show on slider
     * @param min Minimum value for slider
     * @param max Maximum value for slider
     * @return Slider value
     */
    public static float SliderFloat(String label, float displayValue, float min, float max) {
        float newFloat = displayValue;

        float[] imFloat = { displayValue };
        if (ImGui.sliderFloat(label, imFloat, min, max)) {
            newFloat = imFloat[0];
        }

        return newFloat;
    }

    /**
     * Selectable checkbox
     * @param label Text label
     * @param displayValue Display boolean
     * @return Is checkbox checked
     */
    public static boolean Checkbox(String label, boolean displayValue) {
        boolean newBoolean = displayValue;

        if (ImGui.checkbox(label, displayValue)) {
            newBoolean = !displayValue;
        }

        return newBoolean;
    }

    /**
     * Simple text input
     * @param label Text label
     * @param displayValue Input text
     * @return Input text
     */
    public static String InputString(String label, String displayValue) {
        String newString = displayValue;

        ImString outString = new ImString(displayValue, 256);
        if (ImGui.inputText(label, outString)) {
            newString = outString.get();
        }

        return newString;
    }

    /**
     * 2 drag floats
     * @param label Text label
     * @param displayVector Displaying vector 2
     * @return Vector2 value of drag floats
     */
    public static Vector2 DragVector2(String label, Vector2 displayVector) {
        Vector2 newVector = displayVector;

        float[] imVec = { displayVector.x, displayVector.y };
        if (ImGui.dragFloat2(label, imVec)) {
            newVector.Set(imVec[0], imVec[1]);
        }

        return newVector;
    }

    /**
     * 3 drag floats
     * @param label Text label
     * @param displayVector Displaying Vector3
     * @return Vector3 value of drag floats
     */
    public static Vector3 DragVector3(String label, Vector3 displayVector) {
        Vector3 newVector = displayVector;

        float[] imVec = { displayVector.x, displayVector.y, displayVector.z };
        if (ImGui.dragFloat3(label, imVec)) {
            newVector.Set(imVec[0], imVec[1], imVec[2]);
        }

        return newVector;
    }

    /**
     * Color picker + 4 drag ints
     * @param label Text label
     * @param displayColor Displaying color
     * @return Color picker color
     */
    public static Color ColorField(String label, Color displayColor) {
        Color newColor = displayColor;

        float[] imColor = { displayColor.r, displayColor.g, displayColor.b, displayColor.a };
        if (ImGui.colorEdit4(label, imColor)) {
            newColor.Set(imColor[0], imColor[1], imColor[2], imColor[3]);
        }

        return newColor;
    }

    /**
     * Color picker + 4 drag ints
     * @param label Text label
     * @param displayColor Displaying color
     * @param flags ImGuiColorEdit flags
     * @return Color picker color
     */
    public static Color ColorField(String label, Color displayColor, int flags) {
        Color newColor = displayColor;

        float[] imColor = { displayColor.r, displayColor.g, displayColor.b, displayColor.a };
        if (ImGui.colorEdit4(label, imColor, flags)) {
            newColor.Set(imColor[0], imColor[1], imColor[2], imColor[3]);
        }

        return newColor;
    }

    /**
     * Button + Label with selecting textures, using native file explorer
     * @param displayTexture Displaying texture
     * @return Native file explorer texture (MAY BE NULL)
     */
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

    /**
     * Creates combo box from enum
     * @param label Text label
     * @param displayValue Enum value
     * @param displayEnum Enum values
     * @return Enum.EnumProperty
     */
    public static Object EnumSelect(String label, int displayValue, Class displayEnum) {
        Object value = displayEnum.getEnumConstants()[displayValue];
        String[] enumValues = EnumUtility.GetValues(displayEnum);

        if (value == null && displayEnum.getEnumConstants().length > 0) {
            value = displayEnum.getEnumConstants()[0];
        } else if (displayEnum.getEnumConstants().length <= 0) {
            System.err.println("Cannot have an empty enum, must contain at least one attribute.");
        }

        if (value != null) {
            String enumType = ((Enum)value).name();
            ImInt index = new ImInt(EnumUtility.GetIndex(enumType, enumValues));

            if (ImGui.combo(label, index, enumValues, enumValues.length)) {
                return displayEnum.getEnumConstants()[index.get()];
            }
        }

        return displayEnum.getEnumConstants()[displayValue];
    }

    public static File FileReceive(String[] allowedTypes, String typeName, File displayValue) {
        File val = null;
        if (ImGui.treeNodeEx("(" + typeName + ") " + displayValue.getName(), ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.SpanAvailWidth)) {
            if (ImGui.beginDragDropTarget()) {
                if (ImGui.isMouseReleased(0)) {
                    Object newFile = ImGui.getDragDropPayload();

                    if (newFile.getClass().isAssignableFrom(File.class)) {
                        if (FileUtility.IsFileType((File) newFile, allowedTypes)) {
                            val = (File) newFile;
                        } else {
                            Console.Error("File with type" + FileUtility.GetFileExtension((File) newFile) + " not allowed");
                        }
                    }
                }

                ImGui.endDragDropTarget();
            }

            ImGui.treePop();
        }

        return val;
    }

}
