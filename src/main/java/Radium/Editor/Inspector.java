package Radium.Editor;

import Radium.Engine.Application;
import Radium.Engine.Component;
import Radium.Engine.Components.Physics.Rigidbody;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Input.Input;
import Radium.Engine.Input.Keys;
import Radium.Engine.Math.Transform;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.Groups.Group;
import Radium.Engine.Objects.Groups.Groups;
import Radium.Engine.PerformanceImpact;
import Radium.Engine.Physics.PhysxUtil;
import Radium.Engine.Util.FileUtility;
import Radium.Editor.Clipboard.Clipboard;
import Radium.Engine.Window;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import physx.common.PxTransform;

import java.io.File;
import java.util.*;

/**
 * Renders game object properties and components
 */
public class Inspector {

    private static boolean componentChooserOpen = false, groupCreator = false;
    private static Vector2 groupCreatorSize = new Vector2(300, 100);
    private static String groupCreateName = "";
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

    private static final List<Component> components = new ArrayList<>();
    private static final List<List<Component>> submenus = new ArrayList<>();

    private static float nameTextWidth = -1;

    protected Inspector() {}

    /**
     * Initialize textures
     */
    public static void Initialize() {
        transformIcon = new Texture("EngineAssets/Editor/Icons/transform.png", true).GetTextureID();

        ReloadScripts();
    }

    /**
     * Reloads the component add menu objects
     */
    public static void ReloadScripts() {
        components.clear();

        Set<Class<? extends Component>> comps = Application.reflections.getSubTypesOf(Component.class);
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
                if (Objects.equals(comp.submenu, menu)) {
                    subs.add(comp);
                }
            }

            if (!submenus.contains(subs)) {
                submenus.add(subs);
            }
        }

        Sort();
    }

    private static void Sort() {
        for (List<Component> sub : submenus) {
            sub.sort(Comparator.comparing(a -> a.name));
        }

        Collections.sort(submenus, new Comparator<List<Component>>() {
            @Override
            public int compare(List<Component> o1, List<Component> o2) {
                String sm1 = o1.get(0).submenu;
                String sm2 = o2.get(0).submenu;

                return sm1.compareTo(sm2);
            }
        });
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
            if (nameTextWidth == -1) {
                ImVec2 dest = new ImVec2(0, 0);
                ImGui.calcTextSize(dest, "Name");
                nameTextWidth = dest.x;
            }

            SceneHierarchy.current.SetActive(EditorGUI.Checkbox("##active", SceneHierarchy.current.IsActive()));
            ImGui.sameLine();
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - nameTextWidth);
            SceneHierarchy.current.name = EditorGUI.InputString("Name", SceneHierarchy.current.name);

            String newGroup = EditorGUI.Dropdown("Group", SceneHierarchy.current.group.index, Groups.GetGroupList());
            if (newGroup != null) {
                if (newGroup.equals(Groups.AddGroupID)) {
                    groupCreator = true;
                    ImGui.openPopup("Create Group");
                } else {
                    SceneHierarchy.current.group = Groups.GetGroup(newGroup);
                }
            }

            if (groupCreator) {
                ImGui.setNextWindowSize(groupCreatorSize.x, groupCreatorSize.y);
                ImGui.setNextWindowPos((Window.width / 2) - (groupCreatorSize.x / 2), (Window.height / 2) - (groupCreatorSize.y / 2));
                if (ImGui.beginPopupModal("Create Group", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
                    groupCreateName = EditorGUI.InputString("Name", groupCreateName);

                    if (ImGui.button("Add")) {
                        SceneHierarchy.current.group = Group.CreateGroup(groupCreateName);
                        Console.Log(SceneHierarchy.current.group.name);
                        Console.Log(SceneHierarchy.current.group.index);
                        groupCreateName = "";
                        ImGui.closeCurrentPopup();
                        groupCreator = false;
                    }
                    ImGui.sameLine();
                    if (ImGui.button("Cancel")) {
                        ImGui.closeCurrentPopup();
                        groupCreator = false;
                    }
                    ImGui.endPopup();
                }
            }

            ImGui.image(transformIcon, 20, 20);

            ImGui.sameLine();
            if (ImGui.treeNodeEx("Transform", ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth)) {
                boolean open = false;
                if (ImGui.isItemClicked(1)) {
                    open = true;
                }

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
                boolean updateCollider = ImGui.dragFloat3("Scale", sca, precision);

                SceneHierarchy.current.transform.localPosition = FromFloatArray(pos);
                SceneHierarchy.current.transform.localRotation = FromFloatArray(rot);
                SceneHierarchy.current.transform.localScale = FromFloatArray(sca);

                if (updateCollider) {
                    SceneHierarchy.current.CreatePhysicsBody();
                }

                ImGui.treePop();
                if (open) {
                    Clipboard.OpenCopyPasteMenu();
                }
            } else {
                if (ImGui.isItemClicked(1)) {
                    Clipboard.OpenCopyPasteMenu();
                }
            }
            Clipboard.CopyPasteMenu(SceneHierarchy.current.transform, () -> {
                Transform clip = Clipboard.GetClipboardAs(Transform.class);
                if (clip != null) {
                    SceneHierarchy.current.transform.localPosition = clip.localPosition;
                    SceneHierarchy.current.transform.localRotation = clip.localRotation;
                    SceneHierarchy.current.transform.localScale = clip.localScale;

                    SceneHierarchy.current.CreatePhysicsBody();
                }
            });

            int index = 0;
            List<Component> componentsToBeRemoved = new ArrayList<Component>();

            for (int i = 0; i < SceneHierarchy.current.GetComponents().size(); i++) {
                Component c = SceneHierarchy.current.GetComponents().get(i);
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
