package Editor;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class Console {

    private static List<String> logs = new ArrayList<>();

    public static void Render() {
        ImGui.begin("Console", ImGuiWindowFlags.NoCollapse);

        for (String log : logs) {
            ImGui.text(log);
        }

        ImGui.end();
    }

    public static void WriteLine(String message) {
        logs.add(message);

        if (logs.size() > 100) {
            logs.remove(0);
        }
    }

}
