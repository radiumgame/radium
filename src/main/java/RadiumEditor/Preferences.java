package RadiumEditor;

import Radium.Variables;
import Integration.Discord.DiscordStatus;
import imgui.ImGui;
import imgui.type.ImInt;

public class Preferences {

    private static String[] themeOptions = {
        "Light", "Modern Dark", "Mono Chrome", "Dark 2", "ImGui Dark"
    };

    protected Preferences() {}

    public static boolean Open = false;
    public static void Show() {
        Open = !Open;
    }

    private static ImInt colorChoice = new ImInt(Variables.Settings.Theme);
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
            } else if (colorChoice.get() == 3){
                EditorTheme.Dark();
            } else if (colorChoice.get() == 4) {
                ImGui.styleColorsDark();
            }

            Variables.Settings.Theme = colorChoice.get();
            Variables.Settings.Save("EngineAssets/editor.settings");
        }

        boolean use = EditorGUI.Checkbox("Use Discord Integration", Variables.Settings.UseDiscord);
        if (use != Variables.Settings.UseDiscord) {
            if (use) {
                DiscordStatus.EnableRPC();
            } else {
                DiscordStatus.DisableRPC();
            }
        }

        if (ImGui.button("Close")) {
            Open = false;
        }

        ImGui.end();
    }

}
