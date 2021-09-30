package Editor;

import imgui.ImGui;
import imgui.type.ImInt;

public class Preferences {

    private static String[] themeOptions = {
        "Light", "Modern Dark", "Mono Chrome", "ImGui Dark"
    };

    public static boolean Open = false;
    public static void Show() {
        Open = !Open;
    }

    private static ImInt colorChoice = new ImInt(1);
    public static void Render() {
        if (!Open) return;

        ImGui.begin("Preferences");

        if (ImGui.combo("Color Theme", colorChoice, themeOptions, themeOptions.length))  {
            if (colorChoice.get() == 0) {
                ImGui.styleColorsLight();
            } else if (colorChoice.get() == 1) {
                EditorTheme.ModernDark();
            } else if (colorChoice.get() == 2) {
                EditorTheme.MonoChrome();
            } else if (colorChoice.get() == 3) {
                ImGui.styleColorsDark();
            }
        }

        ImGui.end();
    }

}
