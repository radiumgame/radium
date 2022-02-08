package Radium.EventSystem;

import Radium.EventSystem.Events.Event;
import Radium.Objects.GameObject;

/**
 * Can call an event
 */
public interface EventListener {

    /**
     * Triggered when an event is triggered
     * @param object The object that triggered the event
     * @param event The event that was triggered
     */
    void OnEvent(GameObject object, Event event);

}
