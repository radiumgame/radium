package Editor;

import Editor.Debug.Gizmo.TransformationGizmo;
import Engine.Application;
import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Graphics.Texture;
import Engine.Math.Vector.Vector2;
import Engine.Util.NonInstantiatable;
import Engine.Window;
import imgui.ImGui;
import imgui.ImVec2;

public final class Viewport extends NonInstantiatable {

    private static int Play, NowPlaying, Stop;

    public static boolean ViewportFocused = false, ViewportHovered = false;

    public static void Initialize() {
        Play = new Texture("EngineAssets/Editor/play.png").textureID;
        NowPlaying = new Texture("EngineAssets/Editor/nowplaying.png").textureID;
        Stop = new Texture("EngineAssets/Editor/stop.png").textureID;
    }

    public static void Render() {
        ImGui.begin("Game Viewport");

        ViewportFocused = ImGui.isWindowFocused();
        ViewportHovered = ImGui.isWindowHovered();

        ImVec2 size = GetLargestSizeForViewport();
        ImVec2 position = GetCenteredPositionForViewport(size);
        ImGui.setCursorPos(position.x, position.y);
        ImGui.image(Window.GetFrameBuffer().GetTextureID(), size.x, size.y, 0, 1, 1, 0);

        if (!Application.Playing) {
            if (SceneHierarchy.current != null) {
                TransformationGizmo.Update(size);
            }
        }

        ImGui.end();

        RenderControls();
    }

    private static void RenderControls() {
        ImGui.begin("Viewport Controls");

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
