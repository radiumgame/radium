package Radium.Engine.EventSystem;

import Radium.Engine.EventSystem.Events.Event;
import Radium.Engine.Objects.GameObject;

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
