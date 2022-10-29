package Radium.Engine.SceneManagement;

import Radium.Build;
import Radium.Editor.Debug.GridLines;
import Radium.Engine.Components.Rendering.Light;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Serialization.Serializer;
import Radium.Engine.Serialization.TypeAdapters.TextureDeserializer;
import Radium.Engine.System.Popup;
import Radium.Editor.Annotations.RunInEditMode;
import Radium.Editor.Console;
import Radium.Engine.Application;
import Radium.Engine.Component;
import Radium.Engine.Components.Graphics.MeshRenderer;
import Radium.Engine.EventSystem.EventSystem;
import Radium.Engine.EventSystem.Events.Event;
import Radium.Engine.EventSystem.Events.EventType;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Serialization.TypeAdapters.ComponentSerializer;
import Radium.Engine.Serialization.TypeAdapters.GameObjectDeserializer;
import Radium.Engine.Time;
import Radium.Engine.UI.NanoVG.NVG;
import Radium.Engine.Util.FileUtility;
import Radium.Editor.ProjectExplorer;
import Radium.Editor.SceneHierarchy;
import Radium.Engine.Util.ThreadUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joml.Matrix4f;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Contains data about game objects in scene and can load data from .radium files
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

    public String name;

    public String runtimeScene;

    public static boolean RuntimeSerialization = false;
    private static GameObject[] RuntimeAfterObjects;

    /**
     * Create a scene based on a filepath
     * @param filePath File to load data from
     */
    public Scene(String filePath) {
        file = new File(filePath);

        try {
            if (!file.exists()) file.createNewFile();
        } catch (Exception e) { e.printStackTrace(); }

        name = file.getName().split("[.]")[0];
    }

    public void EditorStart() {
        for (GameObject go : gameObjectsInScene) {
            for (Component comp : go.GetComponents()) {
                if (comp.getClass().isAnnotationPresent(RunInEditMode.class)) comp.Start();
            }
        }
    }

    /**
     * When editor plays, it calls start callbacks
     */
    public void Start() {
        if (Build.Editor) {
            try {
                Popup.OpenLoadingBar("Preparing for play...");
                RuntimeSerialization = true;
                ObjectMapper mapper = Serializer.GetRuntimeMapper();
                runtimeScene = mapper.writeValueAsString(gameObjectsInScene);
                RuntimeAfterObjects = mapper.readValue(runtimeScene, GameObject[].class);
                RuntimeSerialization = false;
                Popup.CloseLoadingBar();
            } catch (Exception e) {
                Console.Error(e);
            }
        }

        for (GameObject go : gameObjectsInScene) {
            go.OnPlay();

            for (int i = 0; i < go.GetComponents().size(); i++) {
                Component comp = go.GetComponents().get(i);
                if (comp == null) continue;
                comp.Start();
            }
        }
    }

    /**
     * When editor play stops, it calls stop callbacks
     */
    public void Stop() {
        Popup.OpenLoadingBar("Resetting scene...");

        RuntimeSerialization = true;

        GameObject selected = SceneHierarchy.current;
        String id = null;
        if (selected != null) {
            id = selected.id;
        }

        GameObject[] clone = new GameObject[gameObjectsInScene.size()];
        gameObjectsInScene.toArray(clone);
        for (GameObject go : clone) {
            go.OnStop();

            for (Component comp : go.GetComponents()) {
                comp.Stop();
            }
        }
        for (GameObject go : clone) {
            go.Destroy();
        }
        gameObjectsInScene.clear();

        try {
            for (GameObject go : RuntimeAfterObjects) {
                gameObjectsInScene.add(go);
                if (go.tempId != null) {
                    GameObject parent = GameObject.Find(go.tempId);
                    go.SetParent(parent);
                }

                go.OnStop();
                for (Component comp : go.GetComponents()) {
                    comp.OnAdd();
                    comp.Stop();
                }
            }

            if (id != null) {
                SceneHierarchy.current = GameObject.Find(id);
            }
        } catch (Exception e) {
            Console.Error(e);
        }

        RuntimeSerialization = false;
        Popup.CloseLoadingBar();
    }

    /**
     * Updates the game objects and their components
     */
    public void Update() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);
            go.transform.Update(go);
            go.Update();

            List<Component> sorted = new ArrayList<>(go.GetComponents());
            sorted.sort(Comparator.comparingInt(c -> c.order));
            for (Component comp : sorted) {
                if (comp.enabled) {
                    comp.EditorUpdate();
                    if (Application.Playing) comp.Update();
                    else {
                        if (comp.getClass().isAnnotationPresent(RunInEditMode.class)) {
                            comp.Update();
                        }
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

    public void ShadowRender(Matrix4f lightSpace, Light light) {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);

            for (Component comp : go.GetComponents()) {
                if (comp.getClass() == MeshRenderer.class) {
                    ((MeshRenderer) comp).ShadowRender(lightSpace, light);
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
            Popup.OpenLoadingBar("Saving scene...");
            ObjectMapper mapper = Serializer.GetMapper();
            if (!file.exists()) file.createNewFile();

            PrintWriter pw = new PrintWriter(file);
            pw.flush();
            pw.close();

            FileUtility.Write(file, mapper.writeValueAsString(gameObjectsInScene));

            EventSystem.Trigger(null, new Event(EventType.SceneSave));
            ProjectExplorer.Refresh();
            Popup.CloseLoadingBar();
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

        boolean editor = Build.Editor;
        try {
            if (editor) Popup.OpenLoadingBar("Loading scene...");
            ObjectMapper mapper = Serializer.GetMapper();
            String result = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

            if (!result.equals("")) {
                GameObject[] objs = mapper.readValue(result, GameObject[].class);
                for (GameObject go : objs) {
                    if (go.tempId != null) {
                        GameObject parent = GameObject.Find(go.tempId);
                        go.SetParent(parent);
                    }
                }
            }

            EditorStart();
            EventSystem.Trigger(null, new Event(EventType.SceneLoad));
            if (editor) Popup.CloseLoadingBar();
        }
        catch (Exception e) {
            e.printStackTrace();
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
