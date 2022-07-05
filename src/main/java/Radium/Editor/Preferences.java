package Radium.Editor;

import Radium.Engine.System.FileExplorer;
import Radium.Engine.Util.FileUtility;
import Radium.Engine.Variables;
import Radium.Integration.Discord.DiscordStatus;
import Radium.Editor.EditorWindows.ThemeEditor;
import imgui.ImGui;
import imgui.type.ImInt;

import java.io.File;

/**
 * Editor preferences
 */
public class Preferences {

    private static String[] themeOptions = {
        "Light", "Modern Dark", "Mono Chrome", "Dark 2", "ImGui Dark", "Choose Custom..."
    };
    public static String themePath = "";

    protected Preferences() {}

    /**
     * Is window open
     */
    public static boolean Open = false;

    /**
     * Shows or hides the window
     */
    public static void Show() {
        Open = !Open;

        if (colorChoice.get() == 5) {
            themeOptions[5] = new File(themePath).getName();
        } else {
            themeOptions[5] = "Choose Custom...";
        }
    }

    private static ImInt colorChoice = new ImInt(Variables.Settings.Theme);
    private static int lastChoice = colorChoice.get();
    /**
     * Renders editor window
     */
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
            } else if (colorChoice.get() == 5) {
                String path = FileExplorer.Choose("thm");
                if (FileExplorer.IsPathValid(path)) {
                    Theme theme = Theme.Load(FileUtility.ReadFile(new File(path)));
                    if (theme != null) {
                        themePath = path;
                        EditorTheme.SetStyle(theme);
                    }
                } else {
                    colorChoice.set(lastChoice);
                }
            }

            lastChoice = colorChoice.get();
            if (colorChoice.get() == 5) {
                themeOptions[5] = new File(themePath).getName();
            } else {
                themeOptions[5] = "Choose Custom...";
            }

            Variables.Settings.Theme = colorChoice.get();
            Variables.Settings.ThemePath = themePath;
            Variables.Settings.Save("EngineAssets/editor.settings");
        }
        ImGui.sameLine();
        if (ImGui.button("Create Custom Theme")) {
            ThemeEditor.Render = true;
        }

        boolean use = EditorGUI.Checkbox("Use Discord Radium.Integration", Variables.Settings.UseDiscord);
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
