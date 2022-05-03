package RadiumEditor;

import Radium.Color;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGuiWindow;

import java.util.ArrayList;
import java.util.List;

public class Console {

    private static List<Log> logs = new ArrayList<>();
    private static int MaxLogSize = 999;

    private static boolean autoScroll = true;

    protected Console() {}

    /**
     * Renders editor window + messages
     */
    public static void Render() {
        ImGui.begin("Console", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.AlwaysAutoResize);

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
            ImGui.textColored(log.color, log.data);
        }

        if (autoScroll && ImGui.getScrollMaxY() > 0) {
            ImGui.setScrollY(ImGui.getScrollMaxY());
        }

        ImGui.end();
    }

    /**
     * Displays white text on console
     * @param message Text content
     */
    public static void Log(Object message) {
        logs.add(new Log(new Color(255, 255, 255, 255), "[LOG] " + message));

        CheckLogSize();
    }

    /**
     * Displays yellow text on console
     * @param message Text content
     */
    public static void Warning(Object message) {
        logs.add(new Log(Color.Yellow(), "[WARNING] " + message));

        CheckLogSize();
    }

    /**
     * Displays red text on console
     * @param message Text content
     */
    public static void Error(Object message) {
        logs.add(new Log(Color.Red(), "[ERROR] " + message));

        CheckLogSize();
    }

    /**
     * Displays red text on console with stack trace
     * @param error Error that occurred
     */
    public static void Error(Exception error) {
        logs.add(new Log(Color.Red(), "[ERROR] " + error.getMessage()));
        logs.add(new Log(Color.Red(), error.getStackTrace()[0].getFileName() + " at line " + error.getStackTrace()[0].getLineNumber()));

        CheckLogSize();
    }

    /**
     * Logs message to console with custom color
     * @param message Text content
     * @param color Text color
     */
    public static void Write(Object message, Color color) {
        logs.add(new Log(color, "[WRITE] " + message));

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

        public Log(Color color, String data) {
            this.color = ImColor.floatToColor(color.r, color.g, color.b, color.a);
            this.data = data;
        }

    }

}
