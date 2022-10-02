package Radium.Editor;

import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Util.FileUtility;
import Radium.Engine.Variables;
import Radium.Integration.Discord.DiscordStatus;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import imgui.ImGui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Editor settings
 */
public class Settings {

    /**
     * Editor appearence/theme
     */
    public int Theme = 1;
    public String ThemePath = "";

    /**
     * Use the {@link DiscordStatus discord} integration
     */
    public boolean UseDiscord = false;
    public float EditorCameraSpeed = 1;
    public float EditorCameraSensitivity = 1;

    /**
     * Saves the editor settings to a file
     * @param filepath Settings file
     */
    public void Save(String filepath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            String json = mapper.writeValueAsString(this);
            FileUtility.Write(new File(filepath), json);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    /**
     * Applies the settings
     */
    public void Enable() {
        if (UseDiscord) { DiscordStatus.EnableRPC(); }

        switch (Theme) {
            case 0 -> ImGui.styleColorsLight();
            case 1 -> EditorTheme.ModernDark();
            case 2 -> EditorTheme.MonoChrome();
            case 3 -> EditorTheme.Dark();
            case 4 -> ImGui.styleColorsDark();
            case 5 -> {
                EditorTheme.SetStyle(Radium.Editor.Theme.Load(FileUtility.ReadFile(new File(ThemePath))));
                Preferences.themePath = ThemePath;
            }
            default -> EditorTheme.ModernDark();
        }

        Variables.EditorCamera.zoomFactor = new Vector3(EditorCameraSpeed, EditorCameraSpeed, EditorCameraSpeed);
        Variables.EditorCamera.SetSensitivity(EditorCameraSensitivity);
    }

    /**
     * Tries to load settings from a file
     * @param filepath Settings file
     * @return Settings instance (if loaded)
     */
    public static Settings TryLoadSettings(String filepath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());

            if (!Files.exists(Paths.get(filepath))) return new Settings();
            Settings settings = mapper.readValue(FileUtility.ReadFile(new File(filepath)), Settings.class);

            return settings;
        } catch (Exception e) {
            Console.Error(e);
            return new Settings();
        }
    }

}
