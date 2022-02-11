package RadiumEditor;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Graphics.Mesh;
import Radium.Input.Input;
import Radium.Input.Keys;
import Radium.ModelLoader;
import Radium.Objects.GameObject;
import Radium.SceneManagement.SceneManager;
import Radium.System.FileExplorer;
import Radium.Util.FileUtility;
import Radium.Variables;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.*;

import java.io.File;

/**
 * GameObject hierarchy
 */
public class SceneHierarchy {

    /**
     * Currently selected game obejct
     */
    public static GameObject current;

    private static boolean hierarchyRightClickMenu = false;
    private static boolean gameobjectRightClickMenu = false;

    private static int renderIndex = 0;
    private static int HeaderColor = ImColor.floatToColor(11f / 255f, 90f / 255f, 113f / 255f, 1f);

    protected SceneHierarchy() {}

    /**
     * Render editor window
     */
    public static void Render() {
        ImGui.begin("Scene Hierarchy", ImGuiWindowFlags.NoCollapse);
        ImGui.beginChild(2);

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
                            GameObject sphere = ModelLoader.LoadModel("EngineAssets/Models/Sphere.fbx");

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

            ImGui.endChild();

            if (ImGui.beginDragDropTarget()) {
                Object payload = ImGui.getDragDropPayload();
                if (payload != null && ImGui.isMouseReleased(0)) {
                    if (payload.getClass().isAssignableFrom(GameObject.class)) {
                        GameObject go = (GameObject) payload;
                        go.RemoveParent();
                    } else if (payload.getClass().isAssignableFrom(File.class)) {
                        File f = (File) payload;
                        String extension = FileUtility.GetFileExtension(f);

                        if (extension.equals("fbx") || extension.equals("obj") || extension.equals("dae")) {
                            ModelLoader.LoadModel(f.getPath());
                        }
                    }
                }

                ImGui.endDragDropTarget();
            }

            ImGui.end();
        }

    private static void RenderGameObject(GameObject gameObject) {
        renderIndex++;
        ImGui.pushID(renderIndex);

        int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.OpenOnArrow;
        if (gameObject.GetChildren().size() == 0) {
            flags |= ImGuiTreeNodeFlags.Leaf;
        }

        if (gameObject == current) {
            ImGui.pushStyleColor(ImGuiCol.Header, HeaderColor);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, HeaderColor);

            flags |= ImGuiTreeNodeFlags.Selected;

            MeshFilter filter = gameObject.GetComponent(MeshFilter.class);
            if (filter != null) {
                filter.Select();
            }
        } else {
            MeshFilter filter = gameObject.GetComponent(MeshFilter.class);
            if (filter != null) {
                filter.UnSelect();
            }
        }

        boolean open = ImGui.treeNodeEx(gameObject.name, flags);

        if (gameObject == current) {
            ImGui.popStyleColor();
            ImGui.popStyleColor();
        }
        ImGui.popID();

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(gameObject);
            ImGui.text(gameObject.name);
            ImGui.endDragDropSource();
        }
        if (ImGui.beginDragDropTarget()) {
            Object payload = ImGui.acceptDragDropPayload(GameObject.class);
            if (payload != null) {
                if (payload.getClass().isAssignableFrom(GameObject.class)) {
                    GameObject obj = (GameObject) payload;

                    obj.SetParent(gameObject);
                }
            }

            ImGui.endDragDropTarget();
        }

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

}
