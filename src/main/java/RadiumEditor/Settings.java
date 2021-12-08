package RadiumEditor;

import Radium.Util.FileUtility;
import Integration.Discord.DiscordStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Settings {

    public int Theme = 1;
    public boolean UseDiscord = false;

    public void Save(String filepath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);

        try {
            FileUtility.Write(new File(filepath), json);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Enable() {
        if (UseDiscord) { DiscordStatus.EnableRPC(); }

        switch (Theme) {
            case 0 -> ImGui.styleColorsLight();
            case 1 -> EditorTheme.ModernDark();
            case 2 -> EditorTheme.MonoChrome();
            case 3 -> EditorTheme.Dark();
            case 4 -> ImGui.styleColorsDark();
        }
    }

    public static Settings TryLoadSettings(String filepath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (!Files.exists(Paths.get(filepath))) return new Settings();
        Settings settings = gson.fromJson(FileUtility.ReadFile(new File(filepath)), Settings.class);

        return settings;
    }

}
