package Radium;

import Radium.EventSystem.EventListener;
import Radium.EventSystem.EventSystem;
import Radium.EventSystem.Events.Event;
import Radium.EventSystem.Events.EventType;
import Radium.Objects.GameObject;
import Radium.SceneManagement.SceneManager;
import Integration.Discord.DiscordStatus;

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

    /**
     * Initialize the event listener
     */
    public void Initialize() {
        EventSystem.RegisterEventListener(this);
    }
    
    @Override
    public void OnEvent(GameObject object, Event event) {
        if (event.GetType() == EventType.SceneLoad) {
            if (DiscordStatus.UseDiscordRichPresence) {
                DiscordStatus.UpdateScene();
            }
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
