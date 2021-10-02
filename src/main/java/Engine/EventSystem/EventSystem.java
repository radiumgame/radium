package Engine.EventSystem;

import Engine.EventSystem.Events.Event;
import Engine.Objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {

    private static List<EventListener> eventListeners = new ArrayList<>();

    public static void RegisterEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public static void Trigger(GameObject object, Event event) {
        for (EventListener o : eventListeners) {
            o.OnEvent(object, event);
        }
    }

}
