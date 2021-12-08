package Radium.EventSystem;

import Radium.EventSystem.Events.Event;
import Radium.Objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {

    private static List<EventListener> eventListeners = new ArrayList<>();

    protected EventSystem() {}

    public static void RegisterEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public static void Trigger(GameObject object, Event event) {
        for (EventListener o : eventListeners) {
            o.OnEvent(object, event);
        }
    }

}
