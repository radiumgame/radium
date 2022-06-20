package RadiumEditor;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Components.Rendering.Camera;
import Radium.Components.Rendering.Light;
import Radium.Graphics.Mesh;
import Radium.Graphics.MeshType;
import Radium.Graphics.Texture;
import Radium.Input.Input;
import Radium.Input.Keys;
import Radium.ModelLoader;
import Radium.Objects.GameObject;
import Radium.Objects.Prefab;
import Radium.SceneManagement.SceneManager;
import Radium.System.FileExplorer;
import Radium.System.Popup;
import Radium.Util.FileUtility;
import Radium.Util.ThreadUtility;
import Radium.Variables;
import RadiumEditor.Clipboard.Clipboard;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private static final int HeaderColor = ImColor.floatToColor(11f / 255f, 90f / 255f, 113f / 255f, 1f);

    private static int Radium;

    protected SceneHierarchy() {}

    public static void Initialize() {
        Radium = new Texture("EngineAssets/Textures/Icon/icon.png").textureID;
    }

    /**
     * Render editor window
     */
    public static void Render() {
        ImGui.begin("Scene Hierarchy", ImGuiWindowFlags.NoCollapse);

        boolean header = ImGui.collapsingHeader("##HiddenLabel", ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.DefaultOpen);
        ImGui.sameLine();
        ImGui.image(Radium, 25, 25);
        ImGui.sameLine();
        ImGui.text(SceneManager.GetCurrentScene().name);

        if (header) {
            ImGui.beginChild(2);
            ImGui.indent();

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
                            MeshFilter mf = new MeshFilter(mesh);
                            mf.SetMeshType(MeshType.Plane);
                            plane.AddComponent(mf);
                            plane.AddComponent(new MeshRenderer());
                            plane.name = "Plane";

                            current = plane;
                            ProjectExplorer.SelectedFile = null;
                        }
                        if (ImGui.menuItem("Cube")) {
                            GameObject cube = ModelLoader.LoadModel("EngineAssets/Models/Cube.fbx");
                            GameObject main = cube.GetChildren().get(0).GetChildren().get(0);
                            main.RemoveParent();
                            cube.Destroy();

                            main.GetComponent(MeshFilter.class).SetMeshType(MeshType.Cube);

                            current = main;
                            ProjectExplorer.SelectedFile = null;
                        }
                        if (ImGui.menuItem("Sphere")) {
                            GameObject sphere = ModelLoader.LoadModel("EngineAssets/Models/Sphere.fbx");
                            GameObject main = sphere.GetChildren().get(0).GetChildren().get(0);

                            Console.Log("|-Sphere");
                            for (GameObject go : main.GetChildren()) {
                                Console.Log("|--" + go.name);

                                for (GameObject go2 : go.GetChildren()) {
                                    Console.Log("|---" + go2.name);

                                    for (GameObject go3 : go2.GetChildren()) {
                                        Console.Log("|----" + go3.name);
                                    }
                                }
                            }

                            main.RemoveParent();
                            sphere.Destroy();

                            current = main;
                            ProjectExplorer.SelectedFile = null;
                        }
                        if (ImGui.menuItem("Custom Model")) {
                            String filepath = FileExplorer.Choose("fbx,obj,gltf;");

                            if (filepath != null) {
                                boolean textures = Popup.YesNo("Would you like to load textures(longer wait time)?");
                                ThreadUtility.Run(() -> {
                                    GameObject custom = ModelLoader.LoadModel(filepath, true, textures);
                                    for (GameObject obj : custom.GetChildren()) {
                                        if (obj.ContainsComponent(MeshFilter.class)) {
                                            obj.GetComponent(MeshFilter.class).SetMeshType(MeshType.Custom);
                                        }
                                    }

                                    current = custom;
                                    ProjectExplorer.SelectedFile = null;
                                }, "Model Loader");
                            }
                        }

                        ImGui.endMenu();
                    }

                    if (ImGui.menuItem("Camera")) {
                        GameObject camera = new GameObject();
                        camera.name = "Camera";
                        camera.AddComponent(new Camera());

                        current = camera;
                        ProjectExplorer.SelectedFile = null;
                    }
                    if (ImGui.menuItem("Light")) {
                        GameObject light = new GameObject();
                        light.name = "Light";
                        light.AddComponent(new Light());

                        current = light;
                        ProjectExplorer.SelectedFile = null;
                    }

                    ImGui.endPopup();
                }
            }
            if (gameobjectRightClickMenu) {
                if (current == null) gameobjectRightClickMenu = false;

                if (ImGui.beginPopup("GameObjectRightClick")) {
                    if (ImGui.menuItem("Create Prefab")) {
                        String path = FileExplorer.Create("prefab");
                        if (path != null) {
                            Prefab.Save(current, path);
                        }
                    }
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

            ImGui.unindent();
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
                            GameObject obj = ModelLoader.LoadModel(f.getPath());
                            for (GameObject child : obj.GetChildren()) {
                                if (child.ContainsComponent(MeshFilter.class)) {
                                    child.GetComponent(MeshFilter.class).SetMeshType(MeshType.Custom);
                                }
                            }
                        } else if (extension.equals("prefab")) {
                            current = new Prefab(f.getAbsolutePath()).Create();
                        }
                    }
                }

                ImGui.endDragDropTarget();
            }
        }

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

        boolean open = ImGui.treeNodeEx(gameObject.id, flags, gameObject.name);

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
