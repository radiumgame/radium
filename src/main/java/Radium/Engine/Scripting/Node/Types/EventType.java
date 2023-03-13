package Radium.Engine.Scripting.Node.Types;

import Radium.Engine.Scripting.Node.Events.NodeEvent;

public class EventType extends NodeIoType {

    public EventType() {
        super("Event", new NodeEvent(true));
    }

    public EventType(boolean enabled) {
        super("Event", new NodeEvent(enabled));
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("Event");
    }

}
