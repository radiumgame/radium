package Radium;

import Integration.API.API;
import Integration.Project.Project;
import Radium.EventSystem.EventListener;
import Radium.EventSystem.EventSystem;
import Radium.EventSystem.Events.Event;
import Radium.EventSystem.Events.EventType;
import Radium.Objects.GameObject;
import Radium.SceneManagement.SceneManager;
import Integration.Discord.DiscordStatus;
import RadiumEditor.EditorWindows.Lighting;
import org.reflections.Reflections;

/**
 * Handles events through event listeners
 */
public class Application implements EventListener {

    /**
     * Application framerate
     */
    public static float FPS = 0;
    /**
     * Is editor playing the scene
     */
    public static boolean Playing = false;

    public static boolean Editor = true;

    public static final Reflections reflections = new Reflections("");

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
            SceneManager.GetCurrentScene().Start();
            Playing = true;
        } else if (event.GetType() == EventType.Stop) {
            SceneManager.GetCurrentScene().Stop();
            Playing = false;
        }
    }

}
