package Editor;

import Engine.Application;
import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Graphics.Texture;
import Engine.Math.Mathf;
import Engine.Math.Matrix4;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector3;
import Engine.Util.NonInstantiatable;
import Engine.Variables;
import Engine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Matrix4f;
import org.lwjgl.system.CallbackI;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Editor extends NonInstantiatable {

    private static Reflections reflections = new Reflections("");
    private static List<EditorWindow> editors = new ArrayList<EditorWindow>();
    public static void Initialize() {
        LoadEditorWindows();
    }

    public static void LoadEditorWindows() {
        editors.clear();

        Set<Class<? extends EditorWindow>> editorWindows = reflections.getSubTypesOf(EditorWindow.class);
        for (Class<? extends EditorWindow> editorWindow : editorWindows) {
            try {
                Object instance = editorWindow.getDeclaredConstructor().newInstance();
                EditorWindow editor = (EditorWindow)instance;
                editors.add(editor);
            }
            catch (Exception e) {
                Console.Error(e);
            }
        }
    }

    public static void RenderEditorWindows() {
        for (EditorWindow window : editors) {
            if (window.Render) window.RenderGUI();
        }
    }

    public static List<EditorWindow> GetAllEditorWindows() {
        return editors;
    }

    public static void SetupDockspace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.width, Window.height);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin(" ", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        ImGui.dockSpace(ImGui.getID("Dockspace"));
    }

}
