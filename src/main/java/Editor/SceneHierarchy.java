package Editor;

import Engine.Components.Graphics.MeshFilter;
import Engine.Components.Graphics.MeshRenderer;
import Engine.Graphics.Mesh;
import Engine.Input;
import Engine.Math.Vector.Vector3;
import Engine.Objects.GameObject;
import Engine.SceneManagement.SceneManager;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.glfw.GLFW;

public class SceneHierarchy {

    public static GameObject current;
    private static boolean hierarchyRightClickMenu = false;
    private static boolean gameobjectRightClickMenu = false;

    public static void Render() {
        ImGui.begin("Scene Hierarchy", ImGuiWindowFlags.NoCollapse);

        int index = 0;
        for (GameObject object : SceneManager.GetCurrentScene().gameObjectsInScene) {
            if (current == object)
                ImGui.pushStyleColor(ImGuiCol.Header, ImColor.floatToColor(65 / 255f, 97 / 255f, 188 / 255f));

            ImGui.pushID(index);

            int flags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.OpenOnArrow;
            ImGui.treeNodeEx(object.name, flags);

            ImGui.popID();

            if (ImGui.beginDragDropTarget()) {
                Object payload = ImGui.acceptDragDropPayloadObject("SceneHierarchy");
                if (payload != null) {
                    if (payload.getClass().isAssignableFrom(GameObject.class)) {
                        GameObject obj = (GameObject) payload;
                    }
                }

                ImGui.endDragDropTarget();
            }
            if (current == object) ImGui.popStyleColor();

            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayloadObject("SceneHierarchy", object);
                ImGui.text(object.name);
                ImGui.endDragDropSource();
            }

            if (Input.GetMouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT) && ImGui.isItemHovered(ImGuiHoveredFlags.None)) {
                current = object;
            }

            ImGui.treePop();

            index++;
        }

        if (Input.GetMouseButton(1) && ImGui.isWindowHovered()) {
            if (!ImGui.isItemHovered()) {
                ImGui.openPopup("SceneViewRightClick");
                hierarchyRightClickMenu = true;
            } else {
                ImGui.openPopup("GameObjectRightClick");
                gameobjectRightClickMenu = true;
            }
        }

        if (hierarchyRightClickMenu) {
            if (ImGui.beginPopup("SceneViewRightClick")) {

                if (ImGui.menuItem("Empty Game Object")) {
                    GameObject go = new GameObject();
                    current = go;
                }

                if (ImGui.beginMenu("Objects")) {
                    if (ImGui.menuItem("Plane")) {
                        Mesh mesh = Mesh.Plane(1, 1, "Assets/Radium/Textures/blank.jpg");
                        GameObject plane = new GameObject();
                        plane.AddComponent(new MeshFilter(mesh));
                        plane.AddComponent(new MeshRenderer());

                        current = plane;
                    }
                    if (ImGui.menuItem("Cube")) {
                        Mesh mesh = Mesh.Cube(1, 1, "Assets/Radium/Textures/blank.jpg");
                        GameObject cube = new GameObject();
                        cube.AddComponent(new MeshFilter(mesh));
                        cube.AddComponent(new MeshRenderer());

                        current = cube;
                    }
                    ImGui.endMenu();
                }

                ImGui.endPopup();
            }
        }
        if (gameobjectRightClickMenu) {
            if (ImGui.beginPopup("GameObjectRightClick")) {
                if (ImGui.menuItem("Delete")) {
                    current.Destroy();
                    current = null;
                }

                ImGui.endPopup();
            }
        }

        ImGui.end();
    }

}
