package Editor;

import Engine.Application;
import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Graphics.Framebuffer;
import Engine.Graphics.Texture;
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

    private static int Play, NowPlaying, Stop;
    public static boolean ViewportFocused = false;

    private static Reflections reflections = new Reflections("");
    private static List<EditorWindow> editors = new ArrayList<EditorWindow>();
    public static void Initialize() {
        LoadEditorWindows();

        Play = new Texture("EngineAssets/Editor/play.png").textureID;
        NowPlaying = new Texture("EngineAssets/Editor/nowplaying.png").textureID;
        Stop = new Texture("EngineAssets/Editor/stop.png").textureID;
    }

    public static void LoadEditorWindows() {
        editors.clear();

        Set<Class<? extends EditorWindow>> editorWindows = reflections.getSubTypesOf(EditorWindow.class);
        for (Class<? extends EditorWindow> editorWindow : editorWindows) {
            try {
                Object instance = editorWindow.newInstance();
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

    public static void Viewport() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.MenuBar);

        ViewportFocused = ImGui.isWindowFocused();

        int textureID = Application.Playing ? NowPlaying : Play;
        ImGui.indent((ImGui.getWindowSizeX() / 2) - 60);
        if (ImGui.imageButton(textureID, 40, 30)) {
            if (!Application.Playing) EventSystem.Trigger(null, new Event(EventType.Play));
        }
        ImGui.sameLine();
        if (ImGui.imageButton(Stop, 40, 30)) {
            if (Application.Playing) EventSystem.Trigger(null, new Event(EventType.Stop));
        }

        ImGui.unindent((ImGui.getWindowSizeX() / 2) - 60);

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
