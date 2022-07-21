package Radium.Editor;

import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Components.Graphics.MeshRenderer;
import Radium.Engine.Components.Rendering.Camera;
import Radium.Engine.Components.Rendering.Light;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.MeshType;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Input.Input;
import Radium.Engine.Input.Keys;
import Radium.Engine.ModelLoader;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Objects.Prefab;
import Radium.Engine.SceneManagement.SceneManager;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.System.Popup;
import Radium.Engine.Util.FileUtility;
import Radium.Engine.Util.ThreadUtility;
import Radium.Engine.Variables;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private static int GameObjectIcon;

    private static int renderIndex = 0;
    private static final int HeaderColor = ImColor.floatToColor(11f / 255f, 90f / 255f, 113f / 255f, 1f);

    private static int Radium;

    protected SceneHierarchy() {}

    public static void Initialize() {
        Radium = new Texture("EngineAssets/Textures/Icon/icon.png").textureID;
        GameObjectIcon = new Texture("EngineAssets/Editor/gameobject.png").textureID;
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

        float cursorPos = 0;
        if (header) {
            ImGui.beginChild(2);
            ImGui.indent();

            float maxHeight = 0;
            for (GameObject obj : SceneManager.GetCurrentScene().gameObjectsInScene) {
                if (obj.GetParent() != null) continue;

                float max = RenderGameObject(obj, maxHeight);
                if (max > maxHeight) maxHeight = max;
            }
            cursorPos = ImGui.getMousePosY();
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
                            Mesh mesh = Mesh.Plane();
                            GameObject plane = new GameObject();
                            MeshFilter mf = new MeshFilter(mesh);
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

                            current = main;
                            ProjectExplorer.SelectedFile = null;
                        }
                        if (ImGui.menuItem("Sphere")) {
                            GameObject sphere = ModelLoader.LoadModel("EngineAssets/Models/Sphere.fbx");
                            GameObject main = sphere.GetChildren().get(0).GetChildren().get(0);

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
                                    GameObject custom = ModelLoader.LoadModel(filepath, true, textures, true);

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
            if (ImGui.isWindowHovered(ImGuiHoveredFlags.ChildWindows) && cursorPos > maxHeight) DragDropWindow();
        }

        ImGui.end();
    }

    private static void DragDropWindow() {
        if (ImGui.beginDragDropTarget()) {
            Object payload = ImGui.getDragDropPayload();
            if (payload != null && ImGui.isMouseReleased(0)) {
                if (payload.getClass().isAssignableFrom(GameObject.class)) {
                    GameObject go = (GameObject) payload;
                    go.RemoveParent();
                }
                else if (payload.getClass().isAssignableFrom(File.class)) {
                    File f = (File) payload;
                    String extension = FileUtility.GetFileExtension(f);

                    if (extension.equals("fbx") || extension.equals("obj") || extension.equals("dae")) {
                        ModelLoader.LoadModel(f.getPath(), true);
                    } else if (extension.equals("prefab")) {
                        current = new Prefab(f.getAbsolutePath()).Create();
                    }
                }
            }

            ImGui.endDragDropTarget();
        }
    }

    private static final HashMap<GameObject, Boolean> Open = new HashMap<GameObject, Boolean>();
    private static float RenderGameObject(GameObject gameObject, float max) {
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

        final float padding = 25;
        ImVec2 ccp = ImGui.getCursorScreenPos();
        ImGui.setCursorScreenPos(ccp.x + padding, ccp.y);
        if (gameObjectsToOpen.contains(gameObject)) {
            ImGui.setNextItemOpen(true);
            gameObjectsToOpen.remove(gameObject);
        }
        boolean open = ImGui.treeNodeEx(gameObject.id, flags, gameObject.name);
        Boolean val = Open.get(gameObject);
        boolean same = true;
        if (val != null) {
            same = open == val;
        }
        Open.put(gameObject, open);

        if (scrollTo == gameObject) {
            ImGui.setScrollHereY();
            scrollTo = null;
        }

        if (gameObject == current) {
            ImGui.popStyleColor();
            ImGui.popStyleColor();
        }
        ImGui.popID();
        if (!same) {
            if (!open && current != null) {
                MeshFilter filter = current.GetComponent(MeshFilter.class);
                if (filter != null) {
                    filter.UnSelect();
                }

                current = gameObject;
            }
        }

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(gameObject);
            ImGui.text(gameObject.name);
            ImGui.endDragDropSource();
        }
        if (ImGui.beginDragDropTarget()) {
            GameObject payload = ImGui.acceptDragDropPayload(GameObject.class);
            if (payload != null) {
                payload.SetParent(gameObject);
            }

            ImGui.endDragDropTarget();
        }

        if (ImGui.isItemClicked(0) && ImGui.isItemHovered()) {
            current = gameObject;
            ProjectExplorer.SelectedFile = null;
        }

        if (open) {
            for (int i = 0; i < gameObject.GetChildren().size(); i++) {
                RenderGameObject(gameObject.GetChildren().get(i), max);
            }
        }

        if (open) {
            ImGui.treePop();
        }

        ImVec2 ccp2 = ImGui.getCursorScreenPos();
        if (ccp2.y > max) {
            max = ccp2.y;
        }

        ImGui.setCursorScreenPos(ccp.x, ccp.y);
        ImGui.image(GameObjectIcon, 25, 25);
        ImGui.setCursorScreenPos(ccp2.x, ccp2.y);

        if (ImGui.isItemClicked(1)) {
            ImGui.openPopup("GameObjectRightClick");
            gameobjectRightClickMenu = true;
        }

        return max;
    }

    private static final List<GameObject> gameObjectsToOpen = new ArrayList<GameObject>();
    public static void OpenTreeNodes(GameObject obj) {
        GameObject parent = obj.GetParent();
        if (parent != null) {
            gameObjectsToOpen.add(parent);
            OpenTreeNodes(parent);
        }
    }

    private static GameObject scrollTo;
    public static void ScrollTo(GameObject obj) {
        scrollTo = obj;
    }

}
