package Radium.Engine;

import Radium.Editor.Console;
import Radium.Engine.Components.Rendering.Camera;
import Radium.Engine.EventSystem.EventListener;
import Radium.Engine.EventSystem.EventSystem;
import Radium.Engine.EventSystem.Events.Event;
import Radium.Engine.EventSystem.Events.EventType;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.SceneManagement.SceneManager;
import Radium.Integration.Discord.DiscordStatus;
import Radium.Editor.EditorWindows.Lighting;
import org.reflections.Reflections;

/**
 * Handles events through event listeners
 */
public class Application implements EventListener {

    /**
     * Application framerate
     */
    public static float FPS = 60;
    /**
     * Is editor playing the scene
     */
    public static boolean Playing = false;

    public static boolean Editor = true;

    public static final Reflections reflections = new Reflections("Radium");

    /**
     * Initialize the event listener
     */
    public void Initialize() {
        EventSystem.RegisterEventListener(this);
    }
    
    
    public void OnEvent(GameObject object, Event event) {
        if (event.GetType() == EventType.SceneLoad) {
            if (DiscordStatus.UseDiscordRichPresence) {
                DiscordStatus.UpdateScene();
            }
        } else if (event.GetType() == EventType.SceneSave) {
            Lighting.SaveLightingSettings();
        }

        if (event.GetType() == EventType.Play) {
           if (Variables.DefaultCamera == null) {
               GameObject camera = new GameObject();
               camera.name = "Camera";
               Camera cam = (Camera)camera.AddComponent(new Camera());
               cam.CalculateMatrices();
               Variables.DefaultCamera = cam;
               Console.Error("Default camera not found, creating new one");
           }

            Time.StartPlay();
            SceneManager.GetCurrentScene().Start();
            Playing = true;
        } else if (event.GetType() == EventType.Stop) {
            SceneManager.GetCurrentScene().Stop();
            Playing = false;
        }
    }

}
