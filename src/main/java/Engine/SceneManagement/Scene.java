package Engine.SceneManagement;

import Editor.Console;
import Engine.Application;
import Engine.Component;
import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Objects.GameObject;
import Engine.Serialization.TypeAdapters.ComponentTypeAdapter;
import Engine.Serialization.TypeAdapters.GameObjectTypeAdapter;
import Engine.Util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Scene {

    public List<GameObject> gameObjectsInScene = new ArrayList<>();
    public File file;

    public Scene() {
        file = new File("EngineAssets/Scenes/scene.radiumscene");
    }

    public Scene(String filePath) {
        file = new File(filePath);
    }

    public void Start() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);

            for (Component comp : go.GetComponents()) {
                comp.Start();
            }
        }
    }

    public void Update() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);

            for (Component comp : go.GetComponents()) {
                if (Application.Playing) comp.Update();
                else {
                    if (comp.RunInEditMode) {
                        comp.Update();
                    }
                }
            }
        }
    }

    public boolean ContainsComponent(Class component) {
        boolean result = false;

        for (GameObject go : gameObjectsInScene) {
            if (go.ContainsComponent(component)) {
                result = true;
            }
        }

        return result;
    }

    public void Save() {
        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                    .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
                    .create();

            if (!file.exists()) file.createNewFile();

            PrintWriter pw = new PrintWriter(file);
            pw.flush();
            pw.close();

            FileUtils.Write(file, gson.toJson(gameObjectsInScene));

            EventSystem.Trigger(null, new Event(EventType.SceneSave));
        }
        catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Load() {
        if (!IsSaved()) return;

        try {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                    .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
                    .create();

            String result = "";
            result = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

            if (result != "") {
                GameObject[] objs = gson.fromJson(result, GameObject[].class);
            }

            EventSystem.Trigger(null, new Event(EventType.SceneLoad));
        }
        catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Unload() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            gameObjectsInScene.get(i).Destroy();
        }
    }

    private boolean IsSaved() {
        return file.exists();
    }
}
