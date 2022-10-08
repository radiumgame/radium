package Radium.Editor;

import Radium.Engine.Audio.AudioClip;
import Radium.Engine.Color.Color;
import Radium.Engine.Color.Gradient;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.Time;
import Radium.Engine.Util.EnumUtility;
import Radium.Engine.Util.FileUtility;
import imgui.*;

import java.io.File;

import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import imgui.type.ImString;

/**
 * Useful methods for showing GUI items such as input and sliders
 */
public class EditorGUI {

    private static int AudioPlay, AudioStop;

    protected EditorGUI() {}

    public static void InitializeIcons() {
        AudioPlay = new Texture("EngineAssets/Editor/play.png").GetTextureID();
        AudioStop = new Texture("EngineAssets/Editor/stop.png").GetTextureID();
    }

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

    public static int DragInt(String label, int displayValue, int min, int max) {
        int[] imInt = { displayValue };
        if (ImGui.dragInt(label, imInt, 1, max, max)) {
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

    public static Vector2 DragVectorInt2(String label, Vector2 displayVector) {
        Vector2 newVector = displayVector;

        int[] imVec = { (int)displayVector.x, (int)displayVector.y };
        if (ImGui.dragInt2(label, imVec)) {
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
        if (displayColor == null) {
            displayColor = new Color(0, 0, 0, 0);
        }
        Color newColor = displayColor;

        float[] imColor = { displayColor.r, displayColor.g, displayColor.b, displayColor.a };
        if (ImGui.colorEdit4(label, imColor)) {
            newColor.Set(imColor[0], imColor[1], imColor[2], imColor[3]);
        }

        return newColor;
    }

    public static Gradient GradientEditor(String label, Gradient gradient) {
        if (ImGui.collapsingHeader(label, ImGuiTreeNodeFlags.SpanAvailWidth)) {
            ImGui.indent();
            ImGui.indent();
            ImGui.spacing();

            if (ImGui.button("Add Key")) {
                gradient.selectedKey = gradient.AddKey(0.5f, new Color(1.0f, 1.0f, 1.0f, 1.0f));
            }
            if (gradient.selectedKey != null) {
                ImGui.sameLine();
                if (ImGui.button("Remove Key")) {
                    if (gradient.RemoveKey(gradient.selectedKey)) {
                        gradient.selectedKey = null;
                    } else {
                        Console.Error("Cannot remove key");
                    }
                }
            }

            ImGui.spacing();
            ImGui.spacing();

            ImDrawList drawList = ImGui.getWindowDrawList();
            ImVec2 pos = ImGui.getCursorScreenPos();
            pos.y += 10.0f;
            float maxWidth = Float.max(250.0f, ImGui.getContentRegionAvailX() - 100.0f);
            float height = 25.0f;

            boolean isDragging = false;
            if (gradient.selectedKey != null) {
                isDragging = DrawGradientKey(gradient, gradient.selectedKey, maxWidth, isDragging);
            }

            drawList.addRectFilled(pos.x, pos.y, pos.x + (maxWidth * gradient.keys.get(0).position), pos.y + height, gradient.keys.get(0).color.AsInt());
            drawList.addRectFilled((pos.x + maxWidth) - (maxWidth * (1 - gradient.keys.get(gradient.keys.size() - 1).position)), pos.y, pos.x + maxWidth, pos.y + height, gradient.keys.get(gradient.keys.size() - 1).color.AsInt());
            for (int i = 0; i < gradient.keys.size() - 1; i++) {
                Gradient.Key key = gradient.keys.get(i);
                Gradient.Key key2 = gradient.keys.get(i + 1);

                ImVec4 bounds = new ImVec4(pos.x + (key.position * maxWidth), pos.y, pos.x + (key2.position * maxWidth), pos.y + height);
                if (i == gradient.keys.size() - 2) {
                    //bounds.z = pos.x + maxWidth;
                }

                if (gradient.selectedKey != key) isDragging = DrawGradientKey(gradient, key, maxWidth, isDragging);
                drawList.addRectFilledMultiColor(bounds.x, bounds.y, bounds.z, bounds.w, gradient.GetColor(key.position).AsInt(), gradient.GetColor(key2.position).AsInt(), gradient.GetColor(key2.position).AsInt(), gradient.GetColor(key.position).AsInt());
            }
            if (gradient.selectedKey != gradient.keys.get(gradient.keys.size() - 1))
                DrawGradientKey(gradient, gradient.keys.get(gradient.keys.size() - 1), maxWidth, isDragging);

            if (gradient.keys.size() == 1) {
                drawList.addRectFilled(pos.x, pos.y, pos.x + maxWidth, pos.y + height, gradient.keys.get(0).color.AsInt());
            }

            ImGui.setCursorPos(ImGui.getCursorPosX() + maxWidth + 5.0f, ImGui.getCursorPosY() + (height / 2));
            ImGui.text(label);

            ImGui.newLine();
            if (gradient.selectedKey != null) {
                gradient.selectedKey.color = ColorField("Color", gradient.selectedKey.color);
            }

            ImGui.unindent();
            ImGui.unindent();
        }

        return gradient;
    }

    private static boolean DrawGradientKey(Gradient gradient, Gradient.Key key, float barWidth, boolean isDragging) {
        ImVec2 pos = ImGui.getCursorScreenPos();
        pos.x += key.position * barWidth;
        ImDrawList drawList = ImGui.getWindowDrawList();

        float bias = 50.0f;
        float vertBias = 25.0f;
        int col = ImColor.floatToColor(1, 1, 1, 1);

        boolean returnVal = isDragging;
        if (!isDragging) {
            boolean hovering = ImGui.isMouseDragging(0) && ImGui.isMouseHoveringRect(pos.x - bias, pos.y - vertBias, pos.x + bias, pos.y + vertBias);
            if (ImGui.isMouseHoveringRect(pos.x - 5, pos.y - 5, pos.x + 5, pos.y + 5)) {
                col = ImColor.floatToColor(0.75f, 0.75f, 0.75f, 1);
                hovering = true;
            }
            if (hovering) {
                if (ImGui.isMouseClicked(0, true)) {
                    float x = ImGui.getIO().getMousePosX();
                    float posX = ImGui.getCursorScreenPosX();
                    if (x < posX) {
                        key.position = 0.0f;
                        gradient.Sort();
                    } else if (x > posX + barWidth) {
                        key.position = 1.0f;
                        gradient.Sort();
                    } else {
                        key.position = (x - posX) / barWidth;
                        gradient.Sort();
                    }

                    gradient.selectedKey = key;
                    returnVal = true;
                }
            }

            if (gradient.selectedKey == key) {
                col = ImColor.floatToColor(1, 1, 0, 1);
            }
        }

        drawList.addTriangleFilled(pos.x - 5, pos.y - 5, pos.x + 5, pos.y - 5, pos.x, pos.y + 5, col);
        return returnVal;
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
    public static Texture TextureField(Texture displayTexture) {
        Texture newTexture = null;

        ImGui.image(displayTexture.GetTextureID(), 90, 90);

        ImGui.sameLine();
        if (ImGui.button("Choose ##Texture" + displayTexture.GetTextureID())) {
            String path = FileExplorer.Choose("png,jpg,bmp;");

            if (FileExplorer.IsPathValid(path)) {
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

    public static String Dropdown(String label, int displayValue, String[] displayEnum) {
        String value = displayEnum[displayValue];

        if (value == null && displayEnum.length > 0) {
            value = displayEnum[0];
        } else if (displayEnum.length <= 0) {
            System.err.println("Cannot have an empty enum, must contain at least one attribute.");
        }

        if (value != null) {
            ImInt val = new ImInt(displayValue);
            if (ImGui.combo(label, val, displayEnum, displayEnum.length)) {
                return displayEnum[val.get()];
            }
        }

        return null;
    }

    public static void AudioPlayer(AudioClip clip) {
        ImDrawList dl = ImGui.getWindowDrawList();
        ImVec2 cp = ImGui.getCursorScreenPos();
        cp.x += 10;
        cp.y += 12;
        float width = ImGui.getContentRegionAvailX() / 2;
        float height = 8;
        float xOffset = clip.position * width;

        clip.UpdateClip();
        boolean hovering = ImGui.isMouseHoveringRect(cp.x + xOffset - height, cp.y - 5, cp.x + xOffset + height, cp.y + height);
        if ((ImGui.isMouseDown(0) && hovering) || clip.dragging) {
            float mpx = ImGui.getMousePosX();
            if (mpx < cp.x) mpx = 0;
            else if (mpx > cp.x + width) mpx = width;
            else {
                mpx = width - (cp.x + width - mpx);
            }

            float pct = mpx / width;
            clip.SetPosition(pct);
            xOffset = mpx;

            clip.dragging = true;
        }

        if (!ImGui.isMouseDown(0)) {
            clip.dragging = false;
        }

        dl.addRectFilled(cp.x, cp.y, cp.x + width, cp.y + height, ImColor.floatToColor(0.6f, 0.6f, 0.6f, 1.0f), 30f);
        dl.addRectFilled(cp.x, cp.y, cp.x + xOffset, cp.y + height, ImColor.floatToColor(0, 0.341176471f, 0.807843137f, 1.0f), 30f);
        dl.addCircleFilled(cp.x + xOffset, cp.y + height / 2, height, ImColor.floatToColor(0.75f, 0.75f, 0.75f, 1.0f));
        dl.addText(cp.x + width + 5, cp.y - 6, ImColor.floatToColor(1, 1, 1, 1), clip.formattedPlayingTime);
        ImVec2 textSize = new ImVec2();
        ImGui.calcTextSize(textSize, clip.formattedPlayingTime);
        ImGui.setCursorScreenPos(cp.x + width + height + textSize.x + 5, cp.y - 12);

        if (ImGui.imageButton(AudioPlay, 25, 25)) {
            clip.Play();
        }
        ImGui.sameLine();
        if (ImGui.imageButton(AudioStop, 25, 25)) {
            clip.Pause();
        }
    }

    public static File FileReceive(String[] allowedTypes, String typeName, File displayValue) {
        File val = null;

        if (ImGui.button("Choose ##" + typeName)) {
            StringBuilder allow = new StringBuilder();
            for (String type : allowedTypes) {
                allow.append(type).append(",");
            }
            allow.append(";");
            String path = FileExplorer.Choose(allow.toString());
            if (FileExplorer.IsPathValid(path)) {
                val = new File(path);
            }
        }
        ImGui.sameLine();
        String label = (displayValue == null) ? "(" + typeName + ") None" : "(" + typeName + ") " + displayValue.getName();
        if (ImGui.treeNodeEx(label, ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.SpanAvailWidth)) {
            if (ImGui.beginDragDropTarget()) {
                if (ImGui.isMouseReleased(0)) {
                    Object newFile = ImGui.getDragDropPayload();

                    if (newFile.getClass().isAssignableFrom(File.class)) {
                        if (FileUtility.IsFileType((File) newFile, allowedTypes)) {
                            val = (File) newFile;
                        } else {
                            Console.Error("File with type " + FileUtility.GetFileExtension((File) newFile) + " not allowed");
                        }
                    }
                }

                ImGui.endDragDropTarget();
            }

            ImGui.treePop();
        }

        return val;
    }

    private static float HoverTime = 0;
    public static void UpdateHover() {
        if (ImGui.isAnyItemHovered()) {
            HoverTime += Time.deltaTime;
        } else {
            HoverTime = 0;
        }
    }

    public static void Tooltip(String text) {
        if (ImGui.isItemHovered() && HoverTime > 0.75f) {
            ImGui.beginTooltip();
            ImGui.setTooltip(text);
            ImGui.endTooltip();
        } else if (!ImGui.isAnyItemHovered()) {
            HoverTime = 0;
        }
    }

    public static GameObject ReceiveGameObject(String name, GameObject display) {
        if (ImGui.treeNodeEx(display == null ? "None (GameObject)" : display.name + " (Game Object)", ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.SpanAvailWidth)) {
            if (ImGui.beginDragDropTarget()) {
                if (ImGui.isMouseReleased(0)) {
                    Object newGameObject = ImGui.getDragDropPayload();

                    if (newGameObject.getClass().isAssignableFrom(GameObject.class)) {
                        display = (GameObject) newGameObject;
                    }
                }

                ImGui.endDragDropTarget();
            }

            ImGui.treePop();
        }
        ImGui.sameLine();
        ImGui.text(name);

        return display;
    }

}
