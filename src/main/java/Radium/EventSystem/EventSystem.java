package Radium.EventSystem;

import Radium.EventSystem.Events.Event;
import Radium.Objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {

    private static List<EventListener> eventListeners = new ArrayList<>();

    protected EventSystem() {}

    /**
     * Registers an event listener that can be triggered
     * @param eventListener The event listener to be added
     */
    public static void RegisterEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }

    /**
     * Triggers an event
     * @param object The object that triggered the event
     * @param event The event to be triggered
     */
    public static void Trigger(GameObject object, Event event) {
        for (EventListener o : eventListeners) {
            o.OnEvent(object, event);
        }
    }

}
