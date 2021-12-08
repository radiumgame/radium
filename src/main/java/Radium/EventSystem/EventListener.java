package Radium.EventSystem;

import Radium.EventSystem.Events.Event;
import Radium.Objects.GameObject;

public interface EventListener {

    void OnEvent(GameObject object, Event event);

}
