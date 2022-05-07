package Radium.Objects;

import Radium.Component;
import Radium.SceneManagement.SceneManager;
import Radium.Serialization.TypeAdapters.ComponentTypeAdapter;
import Radium.Serialization.TypeAdapters.GameObjectTypeAdapter;
import Radium.Util.FileUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Prefab {

    public String filepath;
    public File file;

    public Prefab(String filepath) {
        this.filepath = filepath;
        this.file = new File(filepath);
    }

    public GameObject Create() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
                .setPrettyPrinting()
                .create();
        String content = FileUtility.ReadFile(file);
        GameObject gameObject = gson.fromJson(content, GameObject.class);

        return gameObject;
    }

    public static void Save(GameObject gameObject, String path) {
        if (!Files.exists(Paths.get(path))) {
            FileUtility.Create(path);
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
                .setPrettyPrinting()
                .create();
        String content = gson.toJson(gameObject);
        FileUtility.Write(new File(path), content);
    }

}
