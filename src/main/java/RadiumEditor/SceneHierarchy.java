package RadiumEditor;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Graphics.Mesh;
import Radium.Input.Input;
import Radium.Input.Keys;
import Radium.ModelLoader;
import Radium.Objects.GameObject;
import Radium.SceneManagement.SceneManager;
import Radium.Variables;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;

public class SceneHierarchy {

    public static GameObject current;
    private static boolean hierarchyRightClickMenu = false;
    private static boolean gameobjectRightClickMenu = false;

    protected SceneHierarchy() {}

    public static void Render() {
        ImGui.begin("Scene Hierarchy", ImGuiWindowFlags.NoCollapse);

        int index = 0;
        for (GameObject object : SceneManager.GetCurrentScene().gameObjectsInScene) {
            if (current == object)
                ImGui.pushStyleColor(ImGuiCol.FrameBg, ImColor.floatToColor(65 / 255f, 97 / 255f, 188 / 255f));

            ImGui.pushID(index);

            int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.Leaf;
            ImGui.treeNodeEx(object.name, flags);

            ImGui.popID();

            if (ImGui.beginDragDropTarget()) {
                Object payload = ImGui.acceptDragDropPayload(GameObject.class);
                if (payload != null) {
                    if (payload.getClass().isAssignableFrom(GameObject.class)) {
                        GameObject obj = (GameObject) payload;
                    }
                }

                ImGui.endDragDropTarget();
            }
            if (current == object) ImGui.popStyleColor();

            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload(object);
                ImGui.text(object.name);
                ImGui.endDragDropSource();
            }

            if ((Input.GetMouseButtonReleased(0) || Input.GetMouseButton(1)) && ImGui.isItemHovered(ImGuiHoveredFlags.None)) {
                current = object;
                ProjectExplorer.SelectedFile = null;
            }

            ImGui.treePop();

            if (ImGui.isItemClicked(1) && ImGui.isWindowFocused()) {
                ImGui.openPopup("GameObjectRightClick");
                gameobjectRightClickMenu = true;
            }

            index++;
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

                        current = plane;
                        ProjectExplorer.SelectedFile = null;
                    }
                    if (ImGui.menuItem("Cube")) {
                        Mesh mesh = Mesh.Cube(1, 1);
                        GameObject cube = new GameObject();
                        cube.AddComponent(new MeshFilter(mesh));
                        cube.AddComponent(new MeshRenderer());

                        current = cube;
                        ProjectExplorer.SelectedFile = null;
                    }
                    if (ImGui.menuItem("Sphere")) {
                        Mesh mesh = ModelLoader.LoadModel("EngineAssets/Sphere.fbx")[0];
                        GameObject sphere = new GameObject();
                        sphere.AddComponent(new MeshFilter(mesh));
                        sphere.AddComponent(new MeshRenderer());

                        current = sphere;
                        ProjectExplorer.SelectedFile = null;
                    }

                    ImGui.endMenu();
                }

                ImGui.endPopup();
            }
        }
        if (gameobjectRightClickMenu) {
            if (current == null) gameobjectRightClickMenu = false;

            if (ImGui.beginPopup("GameObjectRightClick")) {
                if (ImGui.menuItem("Delete")) {
                    current.Destroy();
                    current = null;
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

}
