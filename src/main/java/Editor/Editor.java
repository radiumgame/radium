package Editor;

import Engine.Graphics.Framebuffer;
import Engine.Util.NonInstantiatable;
import Engine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Editor extends NonInstantiatable {

    private static Reflections reflections = new Reflections("");
    private static List<EditorWindow> editors = new ArrayList<EditorWindow>();
    public static void Initialize() {
        Set<Class<? extends EditorWindow>> editorWindows = reflections.getSubTypesOf(EditorWindow.class);
        for (Class<? extends EditorWindow> editorWindow : editorWindows) {
            try {
                Object instance = editorWindow.newInstance();
                EditorWindow editor = (EditorWindow)instance;
                editors.add(editor);
            }
            catch (Exception e) {
                e.printStackTrace();
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

    public static void Viewport() {
        ImGui.begin("Game Viewport");

        ImVec2 size = GetLargestSizeForViewport();
        ImVec2 position = GetCenteredPositionForViewport(size);

        ImGui.setCursorPos(position.x, position.y);

        ImGui.image(Window.GetFrameBuffer().GetTextureID(), size.x, size.y, 0, 1, 1, 0);

        ImGui.end();
    }

    private static ImVec2 GetLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / (16.f / 9.f);
        if (aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * (16.f / 9.f);
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private static ImVec2 GetCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2.f) - (aspectSize.x / 2.f);
        float viewportY = (windowSize.y / 2.f) - (aspectSize.y / 2.f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }

}
