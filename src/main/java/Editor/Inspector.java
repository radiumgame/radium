package Editor;

import Engine.Component;
import Engine.Components.Graphics.MeshRenderer;
import Engine.Graphics.Texture;
import Engine.Input;
import Engine.Math.Vector.Vector3;
import Engine.Util.NonInstantiatable;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Inspector extends NonInstantiatable {

    static boolean componentChooserOpen = false;
    static ImString search = new ImString();

    static ImString name = new ImString();
    static float[] pos = ToFloatArray(Vector3.Zero);
    static float[] rot = ToFloatArray(Vector3.Zero);
    static float[] sca = ToFloatArray(Vector3.Zero);

    static int precisionKey = GLFW.GLFW_KEY_LEFT_ALT;
    static float precision;
    static float defaultPrecision = 0.3f;
    static float precise = 0.1f;

    static int transformIcon;

    static List<Component> components = new ArrayList<>();
    static Reflections reflections = new Reflections("");
    public static void Initialize() {
        transformIcon = new Texture("EngineAssets/Editor/Icons/transform.png").textureID;

        Set<Class<? extends Component>> comps = reflections.getSubTypesOf(Component.class);
        for (Class<? extends Component> comp : comps) {
            try {
                Object instance = comp.newInstance();
                Component component = (Component)instance;
                components.add(component);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void Render() {
        if (Input.GetKey(precisionKey)) {
            precision = precise;
        }
        else {
            precision = defaultPrecision;
        }

        ImGui.begin("Inspector", ImGuiWindowFlags.NoCollapse);

        if (SceneHierarchy.current == null) {
            name = new ImString();
        }
        if (SceneHierarchy.current != null) {
            if (ImGui.inputText("Name", name)) {
                SceneHierarchy.current.name = name.get();
            }

            ImGui.image(transformIcon, 20, 20);

            ImGui.sameLine();
            if (ImGui.treeNodeEx("Transform", ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth)) {
                pos = ToFloatArray(SceneHierarchy.current.transform.position);
                rot = ToFloatArray(SceneHierarchy.current.transform.rotation);
                sca = ToFloatArray(SceneHierarchy.current.transform.scale);

                ImGui.dragFloat3("Position", pos, precision);
                ImGui.dragFloat3("Rotation", rot, precision);
                ImGui.dragFloat3("Scale", sca, precision);

                SceneHierarchy.current.transform.position = FromFloatArray(pos);
                SceneHierarchy.current.transform.rotation = FromFloatArray(rot);
                SceneHierarchy.current.transform.scale = FromFloatArray(sca);

                ImGui.treePop();
            }

            int index = 0;
            List<Component> componentsToBeRemoved = new ArrayList<Component>();
            for (Component c : SceneHierarchy.current.GetComponents()) {
                c.Render(index);

                if (c.needsToBeRemoved) componentsToBeRemoved.add(c);

                index++;
            }
            if (componentsToBeRemoved.size() > 0) SceneHierarchy.current.RemoveComponent(componentsToBeRemoved.get(0).getClass());

            if (ImGui.button("Add Component", ImGui.getWindowWidth() - 20, 25)) {
                ImGui.openPopup("ComponentChooser");
                componentChooserOpen = true;
            }

            if (componentChooserOpen) {
                if (ImGui.beginPopup("ComponentChooser")) {

                    ImGui.inputText("Search", search);
                    List<Component> componentsToShow = new ArrayList<Component>();

                    for (Component c : components) {
                        if (c.name.toLowerCase().contains(search.get().toLowerCase())) {
                            componentsToShow.add(c);
                        }
                    }

                    for (Component comp : componentsToShow) {
                        ImGui.image(comp.icon, 20, 20);
                        ImGui.sameLine();
                        if (ImGui.treeNodeEx(comp.name, ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf)) {
                            if (ImGui.isItemClicked()) {
                                SceneHierarchy.current.AddComponent(comp);

                                componentChooserOpen = false;
                                ImGui.closeCurrentPopup();
                            }

                            ImGui.treePop();
                        }
                    }

                    ImGui.endPopup();
                }
            }
        }

        ImGui.end();
    }

    private static float[] ToFloatArray(Vector3 vector) {
        return new float[] { vector.x, vector.y, vector.z };
    }

    private static Vector3 FromFloatArray(float[] array) {
        return new Vector3(array[0], array[1], array[2]);
    }

}
