package Engine;

import Editor.Console;
import Engine.EventSystem.EventListener;
import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Objects.GameObject;
import Engine.SceneManagement.SceneManager;
import Runtime.Runtime;

import java.io.*;

public final class Application implements EventListener {

    public static float FPS = 0;
    public static boolean Playing = false;
    public static boolean IsEditor = false;

    public void Initialize() {
        EventSystem.RegisterEventListener(this);
    }

    @Override
    public void OnEvent(GameObject object, Event event) {
        if (event.GetType() == EventType.Play) {
            SceneManager.GetCurrentScene().Start();
            Playing = true;
        } else if (event.GetType() == EventType.Stop) {
            SceneManager.GetCurrentScene().Stop();
            Playing = false;
        }
    }

}
