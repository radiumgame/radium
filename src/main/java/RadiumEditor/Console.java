package RadiumEditor;

import Radium.Color.Color;
import Radium.Graphics.Texture;
import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class Console {

    private static List<Log> logs = new ArrayList<>();
    private static int MaxLogSize = 999;

    private static int Log, Warning, Error;

    private static boolean autoScroll = true;
    private static Log selectedLog;

    private static boolean StackTracePopup = false;

    protected Console() {}

    public static void Initialize() {
        Log = new Texture("EngineAssets/Editor/Console/log.png").textureID;
        Warning = new Texture("EngineAssets/Editor/Console/warning.png").textureID;
        Error = new Texture("EngineAssets/Editor/Console/error.png").textureID;
    }

    /**
     * Renders editor window + messages
     */
    public static void Render() {
        String title = "Console";
        if (logs.size() > 0) title += " (" + logs.size() + ")";
        ImGui.begin(title + "###CONSOLE", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.AlwaysAutoResize);

        if (ImGui.beginMenuBar()) {
            if (ImGui.menuItem("Clear")) {
                Clear(true);
            }
            if (ImGui.checkbox("Auto Scroll", autoScroll)) {
                autoScroll = !autoScroll;
            }

            ImGui.text("Log Size: " + logs.size());

            ImGui.endMenuBar();
        }

        for (int i = 0; i < logs.size(); i++) {
            Log log = logs.get(i);

            //ImGui.textColored(log.color, log.data);

            float height = 50.0f;
            int color = log.selected ? ImColor.rgbToColor("#3D6FA4") : ImColor.floatToColor(1, 1, 1, 0);

            ImDrawList dl = ImGui.getWindowDrawList();
            ImVec2 pos = ImGui.getCursorScreenPos();
            dl.addRectFilled(pos.x, pos.y, pos.x + ImGui.getWindowWidth(), pos.y + height, color);
            if (ImGui.isMouseHoveringRect(pos.x, pos.y, pos.x + ImGui.getWindowWidth(), pos.y + height)) {
                if (ImGui.isMouseClicked(0, false)) {
                    if (selectedLog != null) {
                        selectedLog.selected = false;
                    }

                    log.selected = true;
                    selectedLog = log;
                }
                if (ImGui.isMouseDoubleClicked(0)) {
                    StackTracePopup = true;
                    ImGui.openPopup("Console Stack Trace");
                }
            }

            ImVec2 dest = new ImVec2();
            ImGui.calcTextSize(dest, log.data);
            float halfHeight = pos.y + (height / 2 - dest.y / 2);

            int icon = log.type == LogType.Log ? Log : log.type == LogType.Warning ? Warning : Error;
            dl.addImage(icon, pos.x + 10, pos.y + 10, pos.x + 40, pos.y + 40);

            dl.addText(pos.x + 45, halfHeight, log.color, log.data);
            ImGui.setCursorScreenPos(pos.x, pos.y + height);
        }

        if (autoScroll && ImGui.getScrollMaxY() > 0) {
            ImGui.setScrollY(ImGui.getScrollMaxY());
        }
        if (StackTracePopup) {
            RenderStackTracePopup();
        }

        ImGui.end();
    }

    private static void RenderStackTracePopup() {
        ImGui.setNextWindowSize(500, 300);
        if (ImGui.beginPopup("Console Stack Trace")) {
            ImGui.pushFont(Gui.largeFont);
            ImGui.text("Stack Trace");
            ImGui.popFont();

            ImGui.separator();
            for (StackTraceElement elem : selectedLog.stackTrace) {
                ImGui.text(elem.toString());
            }

            ImGui.endPopup();
        }
    }

    /**
     * Displays white text on console
     * @param message Text content
     */
    public static void Log(Object message) {
        logs.add(new Log(new Color(255, 255, 255, 255), message.toString(), LogType.Log, new Throwable().getStackTrace()));

        CheckLogSize();
    }

    /**
     * Displays yellow text on console
     * @param message Text content
     */
    public static void Warning(Object message) {
        logs.add(new Log(Color.Yellow(), message.toString(), LogType.Warning, new Throwable().getStackTrace()));

        CheckLogSize();
    }

    /**
     * Displays red text on console
     * @param message Text content
     */
    public static void Error(Object message) {
        logs.add(new Log(Color.Red(), message.toString(), LogType.Error, new Throwable().getStackTrace()));

        CheckLogSize();
    }

    /**
     * Displays red text on console with stack trace
     * @param error Error that occurred
     */
    public static void Error(Exception error) {
        logs.add(new Log(Color.Red(), error.getMessage(), LogType.Error, new Throwable().getStackTrace()));
        logs.add(new Log(Color.Red(), error.getStackTrace()[0].getFileName() + " at line " + error.getStackTrace()[0].getLineNumber(), LogType.Error, new Throwable().getStackTrace()));

        CheckLogSize();
    }

    /**
     * Logs message to console with custom color
     * @param message Text content
     * @param color Text color
     */
    public static void Write(Object message, Color color) {
        logs.add(new Log(color, "[WRITE] " + message, LogType.Log, new Throwable().getStackTrace()));

        CheckLogSize();
    }

    /**
     * Clears all logs in console
     */
    public static void Clear(boolean setScroll) {
        if (setScroll) ImGui.setScrollY(0);
        logs.clear();
    }

    private static void CheckLogSize() {
        if (logs.size() > MaxLogSize) {
            logs.remove(0);
        }
    }

    private static class Log {

        public int color;
        public String data;

        public boolean selected = false;
        public LogType type;

        public StackTraceElement[] stackTrace;

        public Log(Color color, String data, LogType type, StackTraceElement[] stackTrace) {
            this.color = ImColor.floatToColor(color.r, color.g, color.b, color.a);
            this.data = data;
            this.type = type;
            this.stackTrace = stackTrace;
        }

    }

    private enum LogType {

        Log,
        Warning,
        Error,

    }

}
