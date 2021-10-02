package Engine.EventSystem;

import Engine.EventSystem.Events.Event;
import Engine.Objects.GameObject;

public interface EventListener {

    void OnEvent(GameObject object, Event event);

}
