package Radium.Engine;

import Radium.Build;
import Radium.Editor.Files.Parser;
import Radium.Editor.Icons;
import Radium.Engine.Animation.AnimationClip;
import Radium.Engine.Color.Color;
import Radium.Engine.Color.Gradient;
import Radium.Engine.Graphics.Lighting.LightCalculationMode;
import Radium.Engine.Objects.Prefab;
import Radium.Engine.Physics.PhysicsMaterial;
import Radium.Engine.Util.ClassUtility;
import Radium.Editor.Annotations.*;
import Radium.Editor.Clipboard.Clipboard;
import Radium.Editor.Console;
import Radium.Engine.Graphics.Material;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.SceneManagement.SceneManager;
import Radium.Engine.Util.EnumUtility;
import Radium.Editor.EditorGUI;
import java.io.File;

import Radium.Runtime;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.apache.commons.text.WordUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Can be added to game object and contains callbacks
 */
public abstract class Component {

    /**
     * The game object the component is connected to
     */
    public transient GameObject gameObject;

    /**
     * Component name displayed in editor
     */
    public transient String name = getClass().getSimpleName();

    /**
     * Component description displayed in editor
     */
    public transient String description = "";

    /**
     * Displays how the component can affect the application
     */
    public transient PerformanceImpact impact = PerformanceImpact.NotSpecified;

    /**
     * Component icon used in editor
     */
    public transient int icon = new Texture("EngineAssets/Editor/Explorer/java.png", true).GetTextureID();

    /**
     * Editor submenu
     */
    public transient String submenu = "";

    public transient int order = 0;
    public transient boolean Hide = false;

    /**
     * Whether to update or run the component
     */
    public boolean enabled = true;

    /**
     * Empty component constructor
     */
    public Component() {}

    /**
     * Called when game is started
     */
    public void Start() {}

    /**
     * Called every frame
     */
    public void Update() {}

    /**
     * Called when editor playing has stopped
     */
    public void Stop() {}

    /**
     * Called when component is added to object
     */
    public void OnAdd() {}

    /**
     * Called when object is removed from object
     */
    public void OnRemove() {}

    /**
     * Called when a variable is updated in the editor
     */
    public void UpdateVariable(String variableName) {}

    public void OnTransformChanged() {}

    public void EditorUpdate() {}

    /**
     * Called when rendering the GUI for the component
     */
    public void GUIRender() {}

    /**
     * Called after GUI has been rendered
     */
    public void PostGUI() {}

    public void ExecuteGUI(String name) {}

    /**
     * Loads icon from editor icon folder
     * @param name File name
     */
    protected void LoadIcon(String name) {
        if (!Build.Editor) return;
        icon = new Texture("EngineAssets/Editor/Icons/" + name, true).GetTextureID();
    }

    /**
     * Used by editor for removing objects
     */
    public transient boolean needsToBeRemoved = false;
    transient boolean goPopupOpen = false;
    transient boolean popupOpen = false;
    transient ImString goSearch = new ImString();

    public void SetEnabled(boolean enabled) {
        if (this.enabled == enabled) return;

        this.enabled = enabled;
        Runtime.DoDepthTest = true;
        Build.DoDepthTest = true;
    }

    public boolean IsEnabled() {
        return enabled;
    }

    /**
     * Render the for the component
     * @param id ImGui ID
     */
    public void Render(int id) {
        try {
            Field[] fields = this.getClass().getDeclaredFields();

            SetEnabled(EditorGUI.Checkbox("##ComponentEnabled" + id, enabled));

            ImGui.sameLine();
            ImGui.image(icon, 20, 20);

            ImGui.sameLine();
            if (ImGui.treeNodeEx(id, ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth, name)) {
                if (ImGui.isItemClicked(1)) {
                    popupOpen = true;
                    ImGui.openPopup("RightClickPopup");
                }

                if (popupOpen) {
                    if (ImGui.beginPopup("RightClickPopup")) {
                        if (ImGui.menuItem("Copy Values")) {
                            Clipboard.SetClipboard(ClassUtility.Clone(this));
                        }
                        if (ImGui.menuItem("Paste Values")) {
                            Component comp = Clipboard.GetClipboardAs(Component.class);
                            if (comp != null) {
                                ClassUtility.CopyFields(comp, this);
                                UpdateVariable("all");
                            }
                        }
                        if (ImGui.menuItem("Remove Component")) {
                            popupOpen = false;
                            ImGui.closeCurrentPopup();

                            needsToBeRemoved = true;
                        }

                        ImGui.endPopup();
                    }
                }

                for (Field field : fields) {
                    if (field.isAnnotationPresent(ExecuteGUI.class)) {
                        ExecuteGUI executeGUI = field.getAnnotation(ExecuteGUI.class);
                        ExecuteGUI(executeGUI.value());
                    }
                    if (field.isAnnotationPresent(ExecuteGUIS.class)) {
                        ExecuteGUI[] guis = field.getAnnotation(ExecuteGUIS.class).value();
                        for (ExecuteGUI executeGUI : guis) {
                            ExecuteGUI(executeGUI.value());
                        }
                    }

                    boolean variableUpdated = false;
                    boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    if (isPrivate || isStatic || field.getModifiers() == 0) {
                        continue;
                    }

                    Class type = field.getType();
                    Object value = field.get(this);
                    String name = WordUtils.capitalize(field.getName());

                    for (Annotation annotation : field.getAnnotations()) {
                        if (annotation instanceof Header) {
                            Header header = (Header) annotation;
                            ImGui.text(header.value());
                            ImGui.spacing();
                            ImGui.separator();
                            ImGui.spacing();
                        } else if (annotation instanceof Divider) {
                            ImGui.spacing();
                            ImGui.separator();
                            ImGui.spacing();
                        }
                    }

                    if (field.isAnnotationPresent(HideInEditor.class)) {
                        continue;
                    }

                    if (type == int.class) {
                        int val = (int) value;
                        int[] imInt = {val};

                        if (field.isAnnotationPresent(RangeInt.class)) {
                            RangeInt anno = field.getAnnotation(RangeInt.class);
                            if (ImGui.sliderInt(name, imInt, anno.min(), anno.max())) {
                                field.set(this, imInt[0]);
                                variableUpdated = true;
                            }
                        } else {
                            if (ImGui.dragInt(name, imInt)) {
                                field.set(this, imInt[0]);
                                variableUpdated = true;
                            }
                        }
                    } else if (type == float.class) {
                        float val = (float) value;
                        float[] imFloat = {val};

                        if (field.isAnnotationPresent(RangeFloat.class)) {
                            RangeFloat anno = field.getAnnotation(RangeFloat.class);
                            if (ImGui.sliderFloat(name, imFloat, anno.min(), anno.max())) {
                                field.set(this, imFloat[0]);
                                variableUpdated = true;
                            }
                        } else {
                            if (ImGui.dragFloat(name, imFloat)) {
                                field.set(this, imFloat[0]);
                                variableUpdated = true;
                            }
                        }
                    } else if (type == boolean.class) {
                        boolean val = (boolean) value;

                        if (ImGui.checkbox(name, val)) {
                            field.set(this, !val);
                            variableUpdated = true;
                        }
                    } else if (type == String.class) {
                        field.set(this, InputText(field.getName(), (String)value, field));
                    }
                    else if (type == Vector2.class) {
                        Vector2 val = (Vector2) value;

                        if (val == null) val = Vector2.Zero();

                        float[] imVec = {val.x, val.y};
                        if (ImGui.dragFloat2(name, imVec)) {
                            val.Set(imVec[0], imVec[1]);
                            variableUpdated = true;
                        }
                    } else if (type == Vector3.class) {
                        Vector3 val = (Vector3) value;

                        if (val == null) val = Vector3.Zero();

                        float[] imVec = {val.x, val.y, val.z};
                        if (ImGui.dragFloat3(name, imVec)) {
                            val.Set(imVec[0], imVec[1], imVec[2]);
                            variableUpdated = true;
                        }

                        field.set(this, val);
                    } else if (type == Color.class) {
                        Color val = (Color) value;

                        if (val == null) val = new Color(255, 255, 255);

                        float[] imColor = new float[] { val.r, val.g, val.b, val.a };
                        if (ImGui.colorEdit4(name, imColor, ImGuiColorEditFlags.AlphaBar)) {
                            val.Set(imColor[0], imColor[1], imColor[2], imColor[3]);
                            variableUpdated = true;
                        }

                        field.set(this, val);
                    } else if (type == Gradient.class) {
                        Gradient val = (Gradient) value;
                        if (val == null) val = new Gradient();

                        Gradient g = EditorGUI.GradientEditor(name, val);
                        if (val != g) {
                            variableUpdated = true;
                        }

                        field.set(this, g);
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
                            Object payload = ImGui.acceptDragDropPayload(GameObject.class);
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
                    else if (type == Prefab.class) {
                        Prefab val = (Prefab)value;

                        File pass = (val == null) ? null : val.file;
                        File f = EditorGUI.FileReceive(new String[] { "prefab" }, "Prefab", pass, "ComponentPrefab", Parser.prefabs, Icons.GetIcon("prefab"));
                        if (f != null) {
                            field.set(this, new Prefab(f.getAbsolutePath()));
                            variableUpdated = true;
                        }
                    }
                    else if (type == Texture.class) {
                        Texture val = (Texture)value;

                        boolean emptyPath = val.filepath.isEmpty();
                        File f = EditorGUI.FileReceive(new String[] { "png", "jpg", "jpeg", "bmp" }, "Texture", (val == null || emptyPath) ? null : new File(val.filepath), "ComponentTexture", Parser.images, Parser.loadedImages);
                        if (f != null) {
                            field.set(this, new Texture(f.getAbsolutePath(), false));
                            variableUpdated = true;
                        }
                    }
                    else if (type == Material.class) {
                        Material val = (Material) value;

                        if (ImGui.collapsingHeader("Material")) {
                            ImGui.indent();

                            if (ImGui.isItemClicked(1)) {
                                Clipboard.OpenCopyPasteMenu();
                            }

                            val.lightCalculationMode = (LightCalculationMode)EditorGUI.EnumSelect("Light Calculation Mode", val.lightCalculationMode.ordinal(), LightCalculationMode.class);
                            LightCalculationMode lcm = val.lightCalculationMode;

                            File f = EditorGUI.FileReceive(new String[] { "png", "jpg", "jpeg", "bmp" }, "Texture", val.file, "ComponentDiffuseTexture", Parser.images, Parser.loadedImages);
                            if (f != null) {
                                val.DestroyMaterial();
                                val.path = f.getAbsolutePath();
                                val.CreateMaterial();

                                field.set(this, val);
                                variableUpdated = true;
                            }

                            File nor = EditorGUI.FileReceive(new String[] { "png", "jpg", "jpeg", "bmp" }, "Normal Map", val.normalFile, "ComponentNormalTexture", Parser.images, Parser.loadedImages);
                            if (nor != null) {
                                val.DestroyMaterial();
                                val.normalMapPath = nor.getAbsolutePath();
                                val.CreateMaterial();

                                field.set(this, val);
                                variableUpdated = true;
                            }

                            File disp = EditorGUI.FileReceive(new String[] { "png", "jpg", "jpeg", "bmp" }, "Displacement Map", val.displacementFile, "ComponentDisplacementTexture", Parser.images, Parser.loadedImages);
                            if (disp != null) {
                                val.DestroyMaterial();
                                val.displacementMapPath = disp.getAbsolutePath();
                                val.CreateMaterial();

                                field.set(this, val);
                                variableUpdated = true;
                            }

                            if (lcm == LightCalculationMode.Normal) {
                                File spec = EditorGUI.FileReceive(new String[]{"png", "jpg", "jpeg", "bmp"}, "Specular Map", val.specularFile, "ComponentSpecularTexture", Parser.images, Parser.loadedImages);
                                if (spec != null) {
                                    val.DestroyMaterial();
                                    val.specularMapPath = spec.getAbsolutePath();
                                    val.CreateMaterial();

                                    field.set(this, val);
                                    variableUpdated = true;
                                }

                                if (ImGui.checkbox("Specular Lighting", val.specularLighting)) {
                                    val.specularLighting = !val.specularLighting;
                                    field.set(this, val);
                                    variableUpdated = true;
                                }
                            }

                            if (ImGui.checkbox("Use Normal Map", val.useNormalMap)) {
                                val.useNormalMap = !val.useNormalMap;
                                field.set(this, val);
                                variableUpdated = true;
                            }

                            if (val.useNormalMap) {
                                float strength = EditorGUI.SliderFloat("Normal Map Strength", val.normalMapStrength, 0, 1);
                                if (strength != val.normalMapStrength) {
                                    val.normalMapStrength = strength;
                                    field.set(this, val);
                                    variableUpdated = true;
                                }
                            }

                            if (ImGui.checkbox("Use Displacement Map", val.useDisplacementMap)) {
                                val.useDisplacementMap = !val.useDisplacementMap;
                                field.set(this, val);
                                variableUpdated = true;
                            }

                            if (val.useDisplacementMap) {
                                float strength = EditorGUI.SliderFloat("Displacement Map Strength", val.displacementMapStrength, 0, 1);
                                if (strength != val.displacementMapStrength) {
                                    val.displacementMapStrength = strength;
                                    field.set(this, val);
                                    variableUpdated = true;
                                }
                            }

                            if (lcm == LightCalculationMode.Normal) {
                                if (ImGui.checkbox("Use Specular Map", val.useSpecularMap)) {
                                    val.useSpecularMap = !val.useSpecularMap;
                                    field.set(this, val);
                                    variableUpdated = true;
                                }
                            }

                            float[] imColor = { val.color.r, val.color.g, val.color.b, val.color.a };
                            if (ImGui.colorEdit4(name, imColor, ImGuiColorEditFlags.AlphaBar)) {
                                val.color = new Color(imColor[0], imColor[1], imColor[2], imColor[3]);
                                field.set(this, val);

                                variableUpdated = true;
                            }

                            if (lcm == LightCalculationMode.Normal) {
                                float[] imReflectivity = {val.reflectivity};
                                if (ImGui.dragFloat("Reflectivity", imReflectivity)) {
                                    val.reflectivity = imReflectivity[0];
                                    field.set(this, val);

                                    variableUpdated = true;
                                }

                                float[] imShineDamper = {val.shineDamper};
                                if (ImGui.dragFloat("Shine Damper", imShineDamper)) {
                                    val.shineDamper = imShineDamper[0];
                                    field.set(this, val);

                                    variableUpdated = true;
                                }
                            }

                            if (lcm == LightCalculationMode.PBR) {
                                val.glossiness = EditorGUI.DragFloat("Glossiness", val.glossiness);
                                val.metallic = EditorGUI.DragFloat("Metallic", val.metallic);
                                val.fresnel = EditorGUI.DragFloat("Fresnel", val.fresnel);
                            }

                            ImGui.unindent();
                        }
                        else {
                            if (ImGui.isItemClicked(1)) {
                                Clipboard.OpenCopyPasteMenu();
                            }
                        }
                        Clipboard.CopyPasteMenu(val, () -> {
                            Material mat = Clipboard.GetClipboardAs(Material.class);
                            if (mat != null) {
                                try {
                                    field.set(this, Material.Clone(mat));
                                } catch (Exception e) {
                                    Console.Error(e);
                                }
                            }
                        });
                    }
                    else if (type == PhysicsMaterial.class) {
                        PhysicsMaterial val = (PhysicsMaterial)value;

                        if (ImGui.collapsingHeader("Physics Material##" + id)) {
                            ImGui.indent();

                            float friction = EditorGUI.DragFloat("Friction", val.friction);
                            if (val.friction != friction) {
                                val.friction = friction;
                                variableUpdated = true;
                            }
                            float restitution = EditorGUI.DragFloat("Restitution", val.restitution);
                            if (val.restitution != restitution) {
                                val.restitution = restitution;
                                variableUpdated = true;
                            }

                            ImGui.unindent();
                        }
                    } else if (type == AnimationClip.class) {
                        AnimationClip val = (AnimationClip)value;

                        File f = EditorGUI.FileReceive(new String[] { "anim" }, "Radium Animation", (val == null || val.path == null) ? null : new File(val.path), "ComponentAnimation", Parser.animations, Icons.GetIcon("animation"));
                        if (f != null) {
                            if (val == null) val = new AnimationClip();
                            val.path = f.getPath();
                            val.LoadProperties(val.path);

                            variableUpdated = true;
                        }
                    }

                    if (variableUpdated) UpdateVariable(field.getName());
                    if (ImGui.isItemHovered() && field.isAnnotationPresent(Tooltip.class)) {
                        EditorGUI.Tooltip(field.getAnnotation(Tooltip.class).value());
                    }
                }

                GUIRender();

                ImGui.treePop();
            }
        } catch (IllegalAccessException e) {
            Console.Error(e);
        }
    }

    private static List<Component> all = new ArrayList<>();
    private static String[] names;

    protected boolean DidFieldChange(String update, String name) {
        if (update.equals(name)) return true;
        if (update.equals("all")) return true;

        return false;
    }

    /**
     * Initializes all component types
     */
    public static void Initialize() {
        Set<Class<? extends Component>> components = Application.reflections.getSubTypesOf(Component.class);
        for (Class<? extends Component> component : components) {
            try {
                Object instance = component.getDeclaredConstructor().newInstance();
                Component comp = (Component)instance;
                all.add(comp);
            }
            catch (Exception e) {
                Console.Error(e);
            }
        }

        names = new String[all.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = all.get(i).name.toLowerCase();
        }
    }

    /**
     * @return All types of components
     */
    public static List<Component> ComponentTypes() {
        return all;
    }

    /**
     * @return All component names
     */
    public static String[] ComponentNames() {
        return names;
    }

    private String InputText(String label, String text, Field field) {
        ImGui.pushID(label);

        ImString outString = new ImString(text, 256);
        if (ImGui.inputText(label, outString)) {
            ImGui.popID();

            UpdateVariable(field.getName());

            return outString.get();
        }

        ImGui.popID();

        return text;
    }

    public static Class<? extends Component> GetComponentType(String name) {
        for (Component comp : all) {
            if (comp.name.equals(name)) return comp.getClass();
        }

        return null;
    }

}
