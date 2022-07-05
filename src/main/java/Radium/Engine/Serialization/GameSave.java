package Radium.Engine.Serialization;

import Radium.Engine.Util.FileUtility;
import Radium.Editor.Console;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class GameSave {

    public String filename;
    private File path;

    protected HashMap<String, Object> properties = new HashMap<>();

    private static final String DESTINATION = System.getenv("APPDATA") + "/RadiumEngine/Saves/";

    public GameSave(String filename) {
        try {
            if (Files.exists(Paths.get(DESTINATION))) {
                Files.createDirectories(Paths.get(DESTINATION));
            }

            this.filename = filename;
            path = new File(DESTINATION + filename);

            if (!path.exists()) {
                path.createNewFile();
            }
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void AddProperty(String name, Object property) {
        properties.put(name, property);
    }

    public void RemoveProperty(String name) {
        properties.remove(name);
    }

    public <T> T GetProperty(String name) {
        return (T) properties.get(name);
    }

    public void Save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeSpecialFloatingPointValues().create();
        String json = gson.toJson(properties);
        FileUtility.Write(path, json);
    }

    public static GameSave Load(String filename) {
        File f = new File(DESTINATION + filename);
        if (!f.exists()) {
            return new GameSave(filename);
        }

        String content = FileUtility.ReadFile(f);

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeSpecialFloatingPointValues().create();
        HashMap<String, Object> properties = gson.fromJson(content, HashMap.class);

        GameSave save = new GameSave(filename);
        save.properties = properties != null ? properties : new HashMap<>();

        return save;
    }

}
