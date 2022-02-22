package RadiumEditor;

import Radium.Component;
import Radium.Components.Physics.Rigidbody;
import Radium.Graphics.Texture;
import Radium.Input.Input;
import Radium.Input.Keys;
import Radium.Math.Vector.Vector3;
import Radium.PerformanceImpact;
import Radium.Physics.PhysxUtil;
import Radium.Util.FileUtility;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.reflections.Reflections;
import physx.common.PxTransform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Renders game object properties and components
 */
public class Inspector {

    private static boolean componentChooserOpen = false;
    private static ImString search = new ImString();

    private static ImString name = new ImString();
    private static float[] pos = ToFloatArray(Vector3.Zero());
    private static float[] rot = ToFloatArray(Vector3.Zero());
    private static float[] sca = ToFloatArray(Vector3.Zero());

    private static Keys precisionKey = Keys.LeftAlt;
    private static float precision;
    private static float defaultPrecision = 0.3f;
    private static float precise = 0.1f;

    private static int transformIcon;

    private static List<Component> components = new ArrayList<>();
    private static List<List<Component>> submenus = new ArrayList<>();
    private static Reflections reflections = new Reflections("");

    protected Inspector() {}

    /**
     * Initialize textures
     */
    public static void Initialize() {
        transformIcon = new Texture("EngineAssets/Editor/Icons/transform.png").textureID;

        ReloadScripts();
    }

    /**
     * Reloads the component add menu objects
     */
    public static void ReloadScripts() {
        components.clear();

        Set<Class<? extends Component>> comps = reflections.getSubTypesOf(Component.class);
        for (Class<? extends Component> comp : comps) {
            try {
                Object instance = comp.getDeclaredConstructor().newInstance();
                Component component = (Component)instance;
                components.add(component);
            }
            catch (Exception e) {
                Console.Error(e);
            }
        }

        for (Component c : components) {
            String menu = c.submenu;
            List<Component> subs = new ArrayList<>();

            for (Component comp : components) {
                if (comp.submenu == menu) {
                    subs.add(comp);
                }
            }

            if (!submenus.contains(subs)) submenus.add(subs);
        }
    }

    /**
     * Renders editor window
     */
    public static void Render() {
        if (Input.GetKey(precisionKey)) {
            precision = precise;
        }
        else {
            precision = defaultPrecision;
        }

        ImGui.begin("Inspector", ImGuiWindowFlags.NoCollapse);

        if (SceneHierarchy.current != null) {
            SceneHierarchy.current.name = EditorGUI.InputString("Name", SceneHierarchy.current.name);

            ImGui.image(transformIcon, 20, 20);

            ImGui.sameLine();
            if (ImGui.treeNodeEx("Transform", ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth)) {
                pos = ToFloatArray(SceneHierarchy.current.transform.localPosition);
                rot = ToFloatArray(SceneHierarchy.current.transform.localRotation);
                sca = ToFloatArray(SceneHierarchy.current.transform.localScale);

                if (ImGui.dragFloat3("Position", pos, precision)) {
                    if (SceneHierarchy.current.ContainsComponent(Rigidbody.class)) {
                        Rigidbody body = SceneHierarchy.current.GetComponent(Rigidbody.class);
                        PxTransform transform = body.GetBody().getGlobalPose();
                        transform.setP(PhysxUtil.ToPx3(FromFloatArray(pos)));
                        body.GetBody().setGlobalPose(transform);
                        body.SetVelocity(Vector3.Zero());
                    }
                }
                if (ImGui.dragFloat3("Rotation", rot, precision)) {
                    if (SceneHierarchy.current.ContainsComponent(Rigidbody.class)) {
                        Rigidbody body = SceneHierarchy.current.GetComponent(Rigidbody.class);
                        PxTransform transform = body.GetBody().getGlobalPose();
                        transform.setQ(PhysxUtil.SetEuler(FromFloatArray(rot)));
                        body.GetBody().setGlobalPose(transform);
                        body.SetAngularVelocity(Vector3.Zero());
                    }
                }
                ImGui.dragFloat3("Scale", sca, precision);

                SceneHierarchy.current.transform.localPosition = FromFloatArray(pos);
                SceneHierarchy.current.transform.localRotation = FromFloatArray(rot);
                SceneHierarchy.current.transform.localScale = FromFloatArray(sca);

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

                    if (search.isEmpty()) {
                        for (int i = 0; i < submenus.size(); i++) {
                            if (submenus.get(i).get(0).submenu != "") {
                                if (ImGui.treeNodeEx(submenus.get(i).get(0).submenu, ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth)) {
                                    for (Component comp : submenus.get(i)) {
                                        ImGui.image(comp.icon, 20, 20);
                                        ImGui.sameLine();
                                        if (ImGui.treeNodeEx(comp.name, ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf)) {
                                            if (ImGui.isItemClicked()) {
                                                try {
                                                    SceneHierarchy.current.AddComponent(comp.getClass().getDeclaredConstructor().newInstance());
                                                } catch (Exception e) {
                                                    Console.Error("Component must contain a default constructor");
                                                }

                                                componentChooserOpen = false;
                                                ImGui.closeCurrentPopup();
                                            }

                                            if (ImGui.isItemHovered()) {
                                                ImGui.beginChild("ComponentDescription", 300, 50, false, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoResize);

                                                ImGui.text("Description: " + (comp.description != "" ? comp.description : "No Description"));

                                                ImGui.separator();

                                                int performanceImpactColor;
                                                if (comp.impact == PerformanceImpact.Low) {
                                                    performanceImpactColor = ImColor.floatToColor(0, 1, 0);
                                                } else if (comp.impact == PerformanceImpact.Medium) {
                                                    performanceImpactColor = ImColor.floatToColor(1, 1, 0);
                                                } else if (comp.impact == PerformanceImpact.High) {
                                                    performanceImpactColor = ImColor.floatToColor(1, 0, 0);
                                                } else {
                                                    performanceImpactColor = ImColor.floatToColor(1, 1, 1);
                                                }
                                                ImGui.text("Performance Impact: ");
                                                ImGui.sameLine();
                                                ImGui.textColored(performanceImpactColor, (comp.impact != PerformanceImpact.NotSpecified) ? comp.impact.toString() : "Not Specified");

                                                ImGui.endChild();
                                            }

                                            ImGui.treePop();
                                        }
                                    }

                                    ImGui.treePop();
                                }
                            } else {
                                for (Component comp : submenus.get(i)) {
                                    ImGui.image(comp.icon, 20, 20);
                                    ImGui.sameLine();
                                    if (ImGui.treeNodeEx(comp.name, ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf)) {
                                        if (ImGui.isItemClicked()) {
                                            try {
                                                SceneHierarchy.current.AddComponent(comp.getClass().getDeclaredConstructor().newInstance());
                                            } catch (Exception e) {
                                                Console.Error("Component must contain a default constructor");
                                            }

                                            componentChooserOpen = false;
                                            ImGui.closeCurrentPopup();
                                        }

                                        if (ImGui.isItemHovered()) {
                                            ImGui.beginChild("ComponentDescription", 300, 50, false, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoResize);

                                            ImGui.text("Description: " + (comp.description != "" ? comp.description : "No Description"));

                                            ImGui.separator();

                                            int performanceImpactColor;
                                            if (comp.impact == PerformanceImpact.Low) {
                                                performanceImpactColor = ImColor.floatToColor(0, 1, 0);
                                            } else if (comp.impact == PerformanceImpact.Medium) {
                                                performanceImpactColor = ImColor.floatToColor(1, 1, 0);
                                            } else if (comp.impact == PerformanceImpact.High) {
                                                performanceImpactColor = ImColor.floatToColor(1, 0, 0);
                                            } else {
                                                performanceImpactColor = ImColor.floatToColor(1, 1, 1);
                                            }
                                            ImGui.text("Performance Impact: ");
                                            ImGui.sameLine();
                                            ImGui.textColored(performanceImpactColor, (comp.impact != PerformanceImpact.NotSpecified) ? comp.impact.toString() : "Not Specified");

                                            ImGui.endChild();
                                        }

                                        ImGui.treePop();
                                    }
                                }
                            }
                        }
                    } else {
                        for (Component comp : components) {
                            if (comp.name.toLowerCase().contains(search.get().toLowerCase())) {
                                ImGui.image(comp.icon, 20, 20);
                                ImGui.sameLine();
                                if (ImGui.treeNodeEx(comp.name, ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf)) {
                                    if (ImGui.isItemClicked()) {
                                        try {
                                            SceneHierarchy.current.AddComponent(comp.getClass().getDeclaredConstructor().newInstance());
                                        } catch (Exception e) {
                                            Console.Error("Component must contain a default constructor");
                                        }

                                        componentChooserOpen = false;
                                        ImGui.closeCurrentPopup();
                                    }

                                    if (ImGui.isItemHovered()) {
                                        ImGui.beginChild("ComponentDescription", 300, 50, false, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoResize);

                                        ImGui.text("Description: " + (comp.description != "" ? comp.description : "No Description"));

                                        ImGui.separator();

                                        int performanceImpactColor;
                                        if (comp.impact == PerformanceImpact.Low) {
                                            performanceImpactColor = ImColor.floatToColor(0, 1, 0);
                                        } else if (comp.impact == PerformanceImpact.Medium) {
                                            performanceImpactColor = ImColor.floatToColor(1, 1, 0);
                                        } else if (comp.impact == PerformanceImpact.High) {
                                            performanceImpactColor = ImColor.floatToColor(1, 0, 0);
                                        } else {
                                            performanceImpactColor = ImColor.floatToColor(1, 1, 1);
                                        }
                                        ImGui.text("Performance Impact: ");
                                        ImGui.sameLine();
                                        ImGui.textColored(performanceImpactColor, (comp.impact != PerformanceImpact.NotSpecified) ? comp.impact.toString() : "Not Specified");

                                        ImGui.endChild();
                                    }

                                    ImGui.treePop();
                                }
                            }
                        }
                    }

                    ImGui.endPopup();
                }
            }
        }
        if (ProjectExplorer.SelectedFile != null) {
            if (ProjectExplorer.SelectedFile.isFile()) {
                ProjectExplorer.FileGUIRender.getOrDefault(FileUtility.GetFileExtension(ProjectExplorer.SelectedFile), (File f) -> { ImGui.text(f.getName()); }).accept(ProjectExplorer.SelectedFile);
            }
        }

        ImGui.end();

        if (SceneHierarchy.current != null) {
            for (Component c : SceneHierarchy.current.GetComponents()) {
                c.PostGUI();
            }
        }
    }

    private static float[] ToFloatArray(Vector3 vector) {
        return new float[] { vector.x, vector.y, vector.z };
    }

    private static Vector3 FromFloatArray(float[] array) {
        return new Vector3(array[0], array[1], array[2]);
    }

}
