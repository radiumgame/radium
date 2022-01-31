package RadiumEditor;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Graphics.Mesh;
import Radium.Input.Input;
import Radium.Input.Keys;
import Radium.Math.Random;
import Radium.ModelLoader;
import Radium.Objects.GameObject;
import Radium.SceneManagement.SceneManager;
import Radium.System.FileExplorer;
import Radium.Variables;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.*;
import imgui.internal.flag.ImGuiItemFlags;
import imgui.type.ImBoolean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SceneHierarchy {

    public static GameObject current;

    private static boolean hierarchyRightClickMenu = false;
    private static boolean gameobjectRightClickMenu = false;
    private static boolean selectOpen = false;

    private static String search = "";
    private static List<GameObject> gameObjectsToShow = new ArrayList<>();
    private static GameObject selectedParentObject;

    private static int renderIndex = 0;
    private static int HeaderColor = ImColor.floatToColor(11f / 255f, 90f / 255f, 113f / 255f, 1f);

    protected SceneHierarchy() {}

    public static void Initialize() {
        UpdateAvailableObjects();
    }

        public static void Render() {
            ImGui.begin("Scene Hierarchy", ImGuiWindowFlags.NoCollapse);

            for (GameObject obj : SceneManager.GetCurrentScene().gameObjectsInScene) {
                if (obj.GetParent() != null) continue;

                RenderGameObject(obj);
            }
            renderIndex = 0;

            if (Input.GetMouseButtonReleased(0) && !ImGui.isAnyItemHovered() && ImGui.isWindowHovered()) {
                SceneHierarchy.current = null;
            }
            if (Input.GetMouseButtonReleased(1) && !ImGui.isAnyItemHovered() && ImGui.isWindowFocused()) {
                if (!ImGui.isItemHovered() && ImGui.isWindowHovered()) {
                    ImGui.openPopup("SceneViewRightClick");
                    hierarchyRightClickMenu = true;
                }
            }

            if (hierarchyRightClickMenu) {
                if (ImGui.beginPopup("SceneViewRightClick")) {

                    if (ImGui.menuItem("Empty Game Object")) {
                        GameObject go = new GameObject();
                        current = go;
                        ProjectExplorer.SelectedFile = null;
                    }

                    if (ImGui.beginMenu("Objects")) {
                        if (ImGui.menuItem("Plane")) {
                            Mesh mesh = Mesh.Plane(1, 1);
                            GameObject plane = new GameObject();
                            plane.AddComponent(new MeshFilter(mesh));
                            plane.AddComponent(new MeshRenderer());
                            plane.name = "Plane";

                            current = plane;
                            ProjectExplorer.SelectedFile = null;
                        }
                        if (ImGui.menuItem("Cube")) {
                            GameObject cube = ModelLoader.LoadModel("EngineAssets/Models/Cube.fbx");

                            current = cube;
                            ProjectExplorer.SelectedFile = null;
                        }
                        if (ImGui.menuItem("Sphere")) {
                            GameObject sphere = ModelLoader.LoadModel("EngineAssets/Models/Sphere.fbx", true);

                            current = sphere;
                            ProjectExplorer.SelectedFile = null;
                        }
                        if (ImGui.menuItem("Custom Model")) {
                            String filepath = FileExplorer.Choose("fbx,obj;");

                            if (filepath != null) {
                                GameObject custom = ModelLoader.LoadModel(filepath);

                                current = custom;
                                ProjectExplorer.SelectedFile = null;
                            }
                        }

                        ImGui.endMenu();
                    }

                    ImGui.endPopup();
                }
            }
            if (gameobjectRightClickMenu) {
                if (current == null) gameobjectRightClickMenu = false;

                boolean changed = false;
                if (ImGui.beginPopup("GameObjectRightClick")) {
                    if (ImGui.menuItem("Set Parent")) {
                        selectOpen = !selectOpen;
                        changed = true;
                    } if (ImGui.menuItem("Delete")) {
                        current.Destroy();
                        current = null;
                    }

                    if (changed) {
                        ImGui.closeCurrentPopup();
                    }

                    ImGui.endPopup();
                }
                if (changed) {
                    ImGui.openPopup("Parent Select");
                }
            }
            if (selectOpen) {
                ImGui.setNextWindowSize(500, 400);
                if (ImGui.beginPopupModal("Parent Select")) {
                    String newSearch = EditorGUI.InputString("Search", search);
                    if (search != newSearch) {
                        search = newSearch;
                        UpdateAvailableObjects();
                    }

                    RenderSelectParent();

                    boolean newParent = false;
                    if (selectedParentObject == null) {
                        ImGui.pushStyleVar(ImGuiStyleVar.Alpha, ImGui.getStyle().getAlpha() * 0.5f);
                    }
                    if (ImGui.button("Select")) {
                        if (selectedParentObject != null) {
                            current.SetParent(selectedParentObject);
                            selectedParentObject = null;
                            selectOpen = false;
                            ImGui.closeCurrentPopup();

                            newParent = true;
                        }
                    }
                    if (selectedParentObject == null && !newParent) {
                        ImGui.popStyleVar();
                    }

                    ImGui.sameLine();
                    if (ImGui.button("Remove Parent")) {
                        current.RemoveParent();
                        selectedParentObject = null;
                        selectOpen = false;
                        ImGui.closeCurrentPopup();
                    }

                    ImGui.sameLine();
                    if (ImGui.button("Cancel")) {
                        selectedParentObject = null;
                        selectOpen = false;
                        ImGui.closeCurrentPopup();
                    }

                    ImGui.endPopup();
                }
            }

            if (Input.GetKey(Keys.F) && Viewport.ViewportFocused) {
                if (current != null) {
                    Variables.EditorCamera.Focus(current);
                }
            }

            Input.SetMouseButtonReleasedFalse(0);
            Input.SetMouseButtonReleasedFalse(1);

            ImGui.end();
        }

    private static void RenderGameObject(GameObject gameObject) {
        renderIndex++;
        ImGui.pushID(renderIndex);

        int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.OpenOnArrow;
        if (gameObject.GetChildren().size() == 0) {
            flags |= ImGuiTreeNodeFlags.Leaf;
        }

        if (gameObject == current) {
            ImGui.pushStyleColor(ImGuiCol.Header, HeaderColor);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, HeaderColor);

            flags |= ImGuiTreeNodeFlags.Selected;
        }

        boolean open = ImGui.treeNodeEx(gameObject.name, flags);

        if (gameObject == current) {
            ImGui.popStyleColor();
            ImGui.popStyleColor();
        }
        ImGui.popID();

        if (ImGui.isItemClicked(0) && ImGui.isItemHovered()) {
            current = gameObject;
            ProjectExplorer.SelectedFile = null;
        }

        if (open) {
            for (int i = 0; i < gameObject.GetChildren().size(); i++) {
                RenderGameObject(gameObject.GetChildren().get(i));
            }
        }

        if (open) {
            ImGui.treePop();
        }

        if (ImGui.isItemClicked(1)) {
            ImGui.openPopup("GameObjectRightClick");
            gameobjectRightClickMenu = true;
        }
    }

    private static void RenderSelectGameObject(GameObject gameObject) {
        ImGui.pushID(renderIndex * 100);

        int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.OpenOnArrow;
        if (gameObject.GetChildren().size() == 0) {
            flags |= ImGuiTreeNodeFlags.Leaf;
        }
        if (gameObject == selectedParentObject) {
            ImGui.pushStyleColor(ImGuiCol.Header, HeaderColor);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, HeaderColor);

            flags |= ImGuiTreeNodeFlags.Selected;
        }

        boolean open = ImGui.treeNodeEx(gameObject.name, flags);
        ImGui.popID();

        if (gameObject == selectedParentObject) {
            ImGui.popStyleColor();
            ImGui.popStyleColor();
        }
        if (ImGui.isItemClicked(0)) {
            selectedParentObject = gameObject;
        }

        if (open) {
            for (int i = 0; i < gameObject.GetChildren().size(); i++) {
                if (gameObject.GetChildren().get(i) == current) {
                    continue;
                }

                RenderSelectGameObject(gameObject.GetChildren().get(i));
            }
        }

        if (open) {
            ImGui.treePop();
        }
    }

    private static void RenderSelectParent() {
        for (GameObject obj : gameObjectsToShow) {
            if (obj.GetParent() != null || obj == current) continue;

            RenderSelectGameObject(obj);
        }
    }

    private static void UpdateAvailableObjects() {
        gameObjectsToShow.clear();
        for (GameObject obj : SceneManager.GetCurrentScene().gameObjectsInScene) {
            if (obj.GetParent() != null) continue;

            if (obj.name.toLowerCase().contains(search.toLowerCase())) {
                gameObjectsToShow.add(obj);
            }
        }
    }

}
