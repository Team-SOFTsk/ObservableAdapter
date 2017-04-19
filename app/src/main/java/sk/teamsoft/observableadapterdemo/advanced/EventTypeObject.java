package sk.teamsoft.observableadapterdemo.advanced;

/**
 * @author Dusan Bartos
 *         Created on 19.04.2017.
 */

public class EventTypeObject {
    private final EventType eventType;
    private final Object data;

    public EventTypeObject(EventType eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Object getData() {
        return data;
    }
}
