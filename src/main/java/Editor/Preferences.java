package Editor;

import Engine.Util.NonInstantiatable;
import Plugins.Discord.DiscordStatus;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;

public final class Preferences extends NonInstantiatable {

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

        boolean use = EditorGUI.Checkbox("Use Discord Integration", DiscordStatus.UseDiscordRichPresence);
        if (use != DiscordStatus.UseDiscordRichPresence) {
            if (use) {
                DiscordStatus.EnableRPC();
            } else {
                DiscordStatus.DisableRPC();
            }

            DiscordStatus.UseDiscordRichPresence = use;
        }

        if (ImGui.button("Close")) {
            Open = false;
        }

        ImGui.end();
    }

}
