package Radium.SceneManagement;

import RadiumEditor.Console;
import Radium.Application;
import Radium.Component;
import Radium.Components.Graphics.MeshRenderer;
import Radium.EventSystem.EventSystem;
import Radium.EventSystem.Events.Event;
import Radium.EventSystem.Events.EventType;
import Radium.Objects.GameObject;
import Radium.Serialization.TypeAdapters.ComponentTypeAdapter;
import Radium.Serialization.TypeAdapters.GameObjectTypeAdapter;
import Radium.Util.FileUtility;
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

    public Scene(String filePath) {
        file = new File(filePath);
    }

    public void Start() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);
            go.OnPlay();

            for (Component comp : go.GetComponents()) {
                comp.Start();
            }
        }
    }

    public void Stop() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);
            go.OnStop();
        }
    }

    public void Update() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);
            go.transform.Update(go);

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

    public void Render() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);

            for (Component comp : go.GetComponents()) {
                if (comp.getClass() == MeshRenderer.class) {
                    comp.Update();
                }
            }
        }
    }

    private boolean CheckGameObjectName(String name) {
        for (GameObject obj : gameObjectsInScene) {
            if (name == obj.name) return false;
        }

        return true;
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
                    .serializeSpecialFloatingPointValues()
                    .create();

            if (!file.exists()) file.createNewFile();

            PrintWriter pw = new PrintWriter(file);
            pw.flush();
            pw.close();

            FileUtility.Write(file, gson.toJson(gameObjectsInScene));

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

            String result = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

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
            gameObjectsInScene.get(i).Destroy(false);
        }

        gameObjectsInScene.clear();
    }

    private boolean IsSaved() {
        return file.exists();
    }
}
