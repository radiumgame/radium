package Engine;

import Editor.Console;
import Engine.Graphics.Texture;
import Engine.Input.Input;
import Engine.Math.Vector.Vector2;
import Engine.Math.Vector.Vector3;
import Engine.Objects.GameObject;
import Engine.SceneManagement.SceneManager;
import Engine.Util.ClassUtility.EnumUtility;
import Engine.Util.FileUtils;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.apache.commons.text.WordUtils;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public abstract class Component implements Cloneable {

    public transient GameObject gameObject;
    public transient String name = getClass().getSimpleName();
    public transient String description = "";
    public transient PerformanceImpact impact = PerformanceImpact.NotSpecified;
    public transient int icon = new Texture("EngineAssets/Editor/Icons/script.png").textureID;
    public transient boolean RunInEditMode = false;

    public abstract void Start();
    public abstract void Update();
    public abstract void Stop();
    public abstract void OnAdd();
    public abstract void OnRemove();
    public abstract void OnVariableUpdate();
    public abstract void GUIRender();

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            Console.Error(e);
            return null;
        }
    }

    public transient boolean needsToBeRemoved = false;
    transient boolean goPopupOpen = false;
    transient boolean popupOpen = false;
    transient ImString goSearch = new ImString();
    public void Render(int id) {
        try {
            Field[] fields = this.getClass().getDeclaredFields();

            ImGui.image(icon, 20, 20);

            ImGui.sameLine();
            if (ImGui.treeNodeEx(id, ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth, name)) {
                if (ImGui.isItemClicked(1)) {
                    popupOpen = true;
                    ImGui.openPopup("RightClickPopup");
                }

                if (popupOpen) {
                    if (ImGui.beginPopup("RightClickPopup")) {

                        if (ImGui.menuItem("Remove Component")) {
                            popupOpen = false;
                            ImGui.closeCurrentPopup();

                            needsToBeRemoved = true;
                        }

                        ImGui.endPopup();
                    }
                }

                boolean variableUpdated = false;
                for (Field field : fields) {
                    boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    if (isPrivate || isStatic) {
                        continue;
                    }

                    Class type = field.getType();
                    Object value = field.get(this);
                    String name = WordUtils.capitalize(field.getName());

                    if (type == int.class) {
                        int val = (int) value;
                        int[] imInt = {val};
                        if (ImGui.dragInt(name, imInt)) {
                            field.set(this, imInt[0]);
                            variableUpdated = true;
                        }
                    } else if (type == float.class) {
                        float val = (float) value;
                        float[] imFloat = {val};
                        if (ImGui.dragFloat(name, imFloat)) {
                            field.set(this, imFloat[0]);
                            variableUpdated = true;
                        }
                    } else if (type == boolean.class) {
                        boolean val = (boolean) value;

                        if (ImGui.checkbox(name, val)) {
                            field.set(this, !val);
                            variableUpdated = true;
                        }
                    } else if (type == String.class) {
                        field.set(this, InputText(field.getName(), (String)value));
                    }
                    else if (type == Vector2.class) {
                        Vector2 val = (Vector2) value;

                        if (val == null) val = Vector2.Zero;

                        float[] imVec = {val.x, val.y};
                        if (ImGui.dragFloat2(name, imVec)) {
                            val.Set(imVec[0], imVec[1]);
                            variableUpdated = true;
                        }
                    } else if (type == Vector3.class) {
                        Vector3 val = (Vector3) value;

                        if (val == null) val = Vector3.Zero;

                        float[] imVec = {val.x, val.y, val.z};
                        if (ImGui.dragFloat3(name, imVec)) {
                            val.Set(imVec[0], imVec[1], imVec[2]);
                            variableUpdated = true;
                        }

                        field.set(this, val);
                    } else if (type == Color.class) {
                        Color val = (Color) value;

                        if (val == null) val = new Color(255, 255, 255);

                        float[] imColor = {val.r, val.g, val.b, val.a};
                        if (ImGui.colorEdit4(name, imColor)) {
                            val.Set(imColor[0], imColor[1], imColor[2], imColor[3]);
                            variableUpdated = true;
                        }

                        field.set(this, val);
                    } else if (type.isEnum()) {
                        String[] enumValues = EnumUtility.GetValues(type);

                        if (value == null && type.getEnumConstants().length > 0) {
                            value = type.getEnumConstants()[0];
                        } else if (type.getEnumConstants().length <= 0) {
                            System.err.println("Cannot have an empty enum, must contain at least one attribute.");
                        }

                        if (value != null) {
                            String enumType = ((Enum) value).name();
                            ImInt index = new ImInt(EnumUtility.GetIndex(enumType, enumValues));

                            if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
                                field.set(this, type.getEnumConstants()[index.get()]);
                                variableUpdated = true;
                            }
                        }
                    } else if (type == GameObject.class) {
                        GameObject val = (GameObject)value;

                        if (ImGui.button("Choose...")) {
                            ImGui.openPopup("GOChooser");
                            goPopupOpen = true;
                        }
                        if (goPopupOpen) {
                            ImGui.beginPopup("GOChooser");

                            ImGui.inputText("Search", goSearch);
                            List<GameObject> goToShow = new ArrayList<GameObject>();
                            for (GameObject go : SceneManager.GetCurrentScene().gameObjectsInScene) {
                                if (go.name.toLowerCase().contains(goSearch.get().toLowerCase())) {
                                    goToShow.add(go);
                                }
                            }

                            for (GameObject go : goToShow) {
                                if (ImGui.treeNodeEx(go.name, ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf)) {
                                    if (ImGui.isItemClicked()) {
                                        val = go;
                                        field.set(this, val);
                                        variableUpdated = true;

                                        goPopupOpen = false;
                                        ImGui.closeCurrentPopup();
                                    }

                                    ImGui.treePop();
                                }
                            }

                            ImGui.endPopup();
                        }

                        ImGui.sameLine();
                        boolean pop = ImGui.treeNodeEx((val == null) ? "(GameObject) None" : "(GameObject) " + val.name, ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf);
                        if (ImGui.beginDragDropTarget()) {
                            Object payload = ImGui.acceptDragDropPayloadObject("SceneHierarchy");
                            if (payload != null) {
                                if (payload.getClass().isAssignableFrom(GameObject.class)) {
                                    GameObject obj = (GameObject) payload;
                                    val = obj;

                                    field.set(this, val);
                                    variableUpdated = true;
                                }
                            }

                            ImGui.endDragDropTarget();
                        }

                        if (pop) ImGui.treePop();
                    }
                }

                GUIRender();

                if (variableUpdated) OnVariableUpdate();

                ImGui.treePop();
            }
        } catch (IllegalAccessException e) {
            Console.Error(e);
        }
    }

    private String InputText(String label, String text) {
        ImGui.pushID(label);

        ImString outString = new ImString(text, 256);
        if (ImGui.inputText(label, outString)) {
            ImGui.popID();

            OnVariableUpdate();

            return outString.get();
        }

        ImGui.popID();

        return text;
    }

}
