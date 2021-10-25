package Editor;

import Engine.Color;
import Engine.Util.NonInstantiatable;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public final class Console extends NonInstantiatable {

    private static List<Log> logs = new ArrayList<>();
    private static int MaxLogSize = 100;

    public static void Render() {
        ImGui.begin("Console", ImGuiWindowFlags.NoCollapse);

        for (int i = 0; i < logs.size(); i++) {
            Log log = logs.get(i);
            ImGui.textColored(log.color, log.data);
        }

        ImGui.end();
    }

    public static void Log(Object message) {
        logs.add(new Log(new Color(255, 255, 255, 255), "[LOG] " + message));

        CheckLogSize();
    }

    public static void Warning(Object message) {
        logs.add(new Log(Color.Yellow(), "[WARNING] " + message));

        CheckLogSize();
    }

    public static void Error(Object message) {
        logs.add(new Log(Color.Red(), "[ERROR] " + message));

        CheckLogSize();
    }

    public static void Error(Exception error) {
        logs.add(new Log(Color.Red(), "[ERROR] " + error.getMessage()));
        logs.add(new Log(Color.Red(), error.getStackTrace()[0].getFileName() + " at line " + error.getStackTrace()[0].getLineNumber()));

        CheckLogSize();
    }

    public static void Write(Object message, Color color) {
        logs.add(new Log(color, "[WRITE] " + message));

        CheckLogSize();
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
