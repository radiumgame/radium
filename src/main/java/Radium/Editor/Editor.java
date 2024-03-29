package Radium.Editor;

import Radium.Engine.Application;
import Radium.Engine.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import java.util.*;

/**
 * Editor window management
 */
public class Editor {

    private static final List<EditorWindow> editors = new ArrayList<EditorWindow>();

    protected Editor() {}

    /**
     * Loads editor windows
     */
    public static void Initialize() {
        LoadEditorWindows();
    }

    /**
     * Loads all classes with extension of {@link EditorWindow EditorWindow}
     */
    public static void LoadEditorWindows() {
        editors.clear();

        Set<Class<? extends EditorWindow>> editorWindows = Application.reflections.getSubTypesOf(EditorWindow.class);
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

        editors.sort(Comparator.comparing(o -> o.MenuName));
    }

    /**
     * Renders all windows with {@link EditorWindow EditorWindow} extension
     */
    public static void RenderEditorWindows() {
        for (EditorWindow window : editors) {
            if (window.Render) {
                if (!window.StartWindow()) continue;
                window.RenderGUI();
                window.EndWindow();
            }
        }
    }

    /**
     * @return All windows with {@link EditorWindow EditorWindow} extension
     */
    public static List<EditorWindow> GetAllEditorWindows() {
        return editors;
    }

    /**
     * Sets up dockspace for all windows
     */
    public static void SetupDockspace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        ImGui.setNextWindowPos(0, 20, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.width, Window.height - 20);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin(" ", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        ImGui.pushStyleColor(ImGuiCol.DockingEmptyBg, 0);
        ImGui.dockSpace(ImGui.getID("Dockspace"));
        ImGui.popStyleColor();
    }

}
