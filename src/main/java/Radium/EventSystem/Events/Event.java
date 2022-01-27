package Radium.EventSystem.Events;

public class Event {

    private EventType eventType;

    public Event(EventType type) {
        eventType = type;
    }

    public EventType GetType() {
        return eventType;
    }

}
