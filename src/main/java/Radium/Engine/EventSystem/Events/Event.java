package Radium.Engine.EventSystem.Events;

/**
 * An event that can be triggered
 */
public class Event {

    private EventType eventType;

    /**
     * Create an event with an event type
     * @param type
     */
    public Event(EventType type) {
        eventType = type;
    }

    /**
     * Returns the events type
     * @return Event type
     */
    public EventType GetType() {
        return eventType;
    }

}
