package RadiumEditor;

import Radium.Math.Vector.Vector2;
import Radium.PostProcessing.PostProcessing;
import Radium.Variables;
import RadiumEditor.Debug.Gizmo.TransformationGizmo;
import Radium.Application;
import Radium.EventSystem.EventSystem;
import Radium.EventSystem.Events.Event;
import Radium.EventSystem.Events.EventType;
import Radium.Graphics.Texture;
import RadiumEditor.MousePicking.MousePicking;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiWindowFlags;

/**
 * The render display, game graphics are rendered here
 */
public class Viewport {

    private static int Play, NowPlaying, Stop;

    /**
     * Is editor window focused
     */
    public static boolean ViewportFocused = false;
    /**
     * Is editor window hovered
     */
    public static boolean ViewportHovered = false;

    public static Vector2 position = Vector2.Zero(), size = Vector2.Zero(), imageSize = Vector2.Zero(), imagePosition = Vector2.Zero();

    private static int Grid;

    protected Viewport() {}

    /**
     * Initializes textures
     */
    public static void Initialize() {
        Play = new Texture("EngineAssets/Editor/play.png").textureID;
        NowPlaying = new Texture("EngineAssets/Editor/nowplaying.png").textureID;
        Stop = new Texture("EngineAssets/Editor/stop.png").textureID;
        Grid = new Texture("EngineAssets/Editor/grid.png").textureID;
    }

    /**
     * Renders the editor window
     */
    public static void Render() {
        RenderControls();

        ImGui.begin("Game Viewport", ImGuiWindowFlags.MenuBar);

        if (ImGui.beginMenuBar()) {
            ImGui.setNextItemWidth(ImGui.getWindowWidth() / 6.5f);
            LocalEditorSettings.ShadeType = (RenderMode)EditorGUI.EnumSelect("##SHADE_TYPE", LocalEditorSettings.ShadeType.ordinal(), RenderMode.class);
            EditorGUI.Tooltip("Shading Mode");

            if (ImGui.imageButton(Grid, 20, 20)) {
                LocalEditorSettings.Grid = !LocalEditorSettings.Grid;
            }
            EditorGUI.Tooltip("Toggle Grid");

            ImGui.endMenuBar();
        }

        ViewportFocused = ImGui.isWindowFocused();
        ViewportHovered = ImGui.isWindowHovered();

        if (Application.Playing) {
            if (Variables.DefaultCamera != null && Variables.DefaultCamera.gameObject != null) {

            } else {
                ImGui.pushFont(Gui.largeFont);
                ImVec2 windowSize = ImGui.getWindowSize();
                ImVec2 textSize = new ImVec2();
                ImGui.calcTextSize(textSize, "Please put a camera in the scene");

                ImGui.setCursorPos((windowSize.x - textSize.x) * 0.5f, (windowSize.y - textSize.y) * 0.5f);
                ImGui.text("Please put a camera in the scene");
                ImGui.popFont();

                ImGui.end();

                return;
            }
        }

        ImVec2 s = GetLargestSizeForViewport();
        ImVec2 p = GetCenteredPositionForViewport(s);

        imageSize = new Vector2(s.x, s.y);
        imagePosition = new Vector2(p.x, p.y);

        ImGui.setCursorPos(p.x, p.y);
        ImGui.image(PostProcessing.GetTexture(), s.x, s.y, 0, 1, 1, 0);

        ImVec2 pos = ImGui.getWindowPos();
        ImVec2 siz = ImGui.getWindowSize();
        Vector2 viewportPos = new Vector2(pos.x, pos.y);
        Vector2 viewportSize = new Vector2(siz.x, siz.y);
        position = viewportPos;
        size = viewportSize;

        if (!Application.Playing) {
            if (SceneHierarchy.current != null) {
                TransformationGizmo.Update(s);
            }
        }

        ImGui.end();
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

        ImGui.indent((ImGui.getWindowSizeX() / 3));
        if (ImGui.radioButton("T", TransformationGizmo.operation == Operation.TRANSLATE)) {
            TransformationGizmo.SetOperation(Operation.TRANSLATE);
        }
        ImGui.sameLine();
        if (ImGui.radioButton("R", TransformationGizmo.operation == Operation.ROTATE)) {
            TransformationGizmo.SetOperation(Operation.ROTATE);
        }
        ImGui.sameLine();
        if (ImGui.radioButton("S", TransformationGizmo.operation == Operation.SCALE)) {
            TransformationGizmo.SetOperation(Operation.SCALE);
        }
        ImGui.unindent((ImGui.getWindowSizeX() / 3));

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
