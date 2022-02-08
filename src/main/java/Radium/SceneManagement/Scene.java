package Radium.SceneManagement;

import RadiumEditor.Annotations.RunInEditMode;
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

/**
 * Contains data about game objects in scene and can load data from .radiumscene files
 */
public class Scene {

    /**
     * All game objects in the scene
     */
    public List<GameObject> gameObjectsInScene = new ArrayList<>();
    /**
     * Scene data is loaded from file
     */
    public File file;

    /**
     * Create a scene based on a filepath
     * @param filePath File to load data from
     */
    public Scene(String filePath) {
        file = new File(filePath);
    }

    /**
     * When editor plays, it calls start callbacks
     */
    public void Start() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);
            go.OnPlay();

            for (Component comp : go.GetComponents()) {
                comp.Start();
            }
        }
    }

    /**
     * When editor play stops, it calls stop callbacks
     */
    public void Stop() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);
            go.OnStop();
        }
    }

    /**
     * Updates the game objects and their components
     */
    public void Update() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);
            go.transform.Update(go);

            for (Component comp : go.GetComponents()) {
                if (Application.Playing) comp.Update();
                else {
                    if (comp.getClass().isAnnotationPresent(RunInEditMode.class)) {
                        comp.Update();
                    }
                }
            }
        }
    }

    /**
     * Updates the mesh renderer components
     */
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

    /**
     * Loops through all objects to check if game object contains a component
     * @param component Type of component
     * @return If scene contains a component
     */
    public boolean ContainsComponent(Class component) {
        boolean result = false;

        for (GameObject go : gameObjectsInScene) {
            if (go.ContainsComponent(component)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Saves scene data to a file
     */
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

    /**
     * Loads the scene data from a file
     */
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

    /**
     * Deletes all scene game objects
     */
    public void Unload() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            gameObjectsInScene.get(i).Destroy(false);
        }

        gameObjectsInScene.clear();
    }

    /**
     * @return sceneFile.exists();
     */
    private boolean IsSaved() {
        return file.exists();
    }
}
