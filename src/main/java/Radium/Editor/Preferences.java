package Radium.Editor;

import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.Util.FileUtility;
import Radium.Engine.Variables;
import Radium.Integration.Discord.DiscordStatus;
import Radium.Editor.EditorWindows.ThemeEditor;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImInt;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Editor preferences
 */
public class Preferences {

    private static String[] themeOptions = {
        "Light", "Modern Dark", "Mono Chrome", "Dark 2", "ImGui Dark", "Choose Custom..."
    };
    public static String themePath = "";

    private static Map<String, Runnable> sidebar = new LinkedHashMap<>();
    private static String selectedSidebarMenu = "";

    protected Preferences() {}

    /**
     * Is window open
     */
    public static boolean Open = false;

    public static void Initialize() {
        sidebar.put("Editor Style", Preferences::SidebarStyleSection);
        sidebar.put("Editor Camera", Preferences::EditorCameraSection);
        sidebar.put("Misc", Preferences::MiscSection);

        selectedSidebarMenu = "Editor Style";
    }

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

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Preferences");

        if (ImGui.beginListBox("##Sidebar", 200, ImGui.getWindowSizeY() - 25)) {
            for (String sidebarOption : sidebar.keySet()) {
                boolean selected = sidebarOption.equals(selectedSidebarMenu);
                if (selected) {
                    ImGui.pushStyleColor(ImGuiCol.Header, ImColor.floatToColor(11f / 255f, 90f / 255f, 113f / 255f, 1f));
                    ImGui.pushStyleColor(ImGuiCol.HeaderHovered, ImColor.floatToColor(11f / 255f, 90f / 255f, 113f / 255f, 1f));
                }

                if (ImGui.selectable(sidebarOption, selected)) {
                    selectedSidebarMenu = sidebarOption;
                }

                if (selected) {
                    ImGui.popStyleColor(2);
                }
            }

            ImGui.endListBox();
        }
        ImGui.popStyleVar(1);
        ImGui.sameLine();
        ImGui.beginChild("section");

        sidebar.get(selectedSidebarMenu).run();

        if (ImGui.button("Save")) {
            Variables.Settings.Save("EngineAssets/editor.settings");
        }
        ImGui.sameLine();
        if (ImGui.button("Close")) {
            Open = false;
        }

        ImGui.endChild();
        ImGui.end();
    }

    private static void SidebarStyleSection() {
        if (ImGui.combo("Color Theme", colorChoice, themeOptions, themeOptions.length)) {
            if (colorChoice.get() == 0) {
                ImGui.styleColorsLight();
            } else if (colorChoice.get() == 1) {
                EditorTheme.ModernDark();
            } else if (colorChoice.get() == 2) {
                EditorTheme.MonoChrome();
            } else if (colorChoice.get() == 3) {
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
    }

    private static void EditorCameraSection() {
        Vector3 camSpeed = Variables.EditorCamera.zoomFactor;
        float speed = EditorGUI.DragFloat("Camera Speed", Variables.Settings.EditorCameraSpeed);
        Variables.Settings.EditorCameraSpeed = speed;
        camSpeed.x = speed;
        camSpeed.y = speed;
        camSpeed.z = speed;

        float sens = EditorGUI.DragFloat("Look Sensitivity", Variables.Settings.EditorCameraSensitivity);
        Variables.Settings.EditorCameraSensitivity = sens;
        Variables.EditorCamera.SetSensitivity(sens);
    }

    private static void MiscSection() {
        boolean use = EditorGUI.Checkbox("Use Discord Radium Integration", Variables.Settings.UseDiscord);
        if (use != Variables.Settings.UseDiscord) {
            if (use) {
                DiscordStatus.EnableRPC();
            } else {
                DiscordStatus.DisableRPC();
            }
        }
    }

}
