package RadiumEditor;

import Radium.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Editor {

    private static Reflections reflections = new Reflections("");
    private static List<EditorWindow> editors = new ArrayList<EditorWindow>();

    protected Editor() {}

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
            if (window.Render) {
                if (!window.StartWindow()) continue;
                window.RenderGUI();
                window.EndWindow();
            }
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
