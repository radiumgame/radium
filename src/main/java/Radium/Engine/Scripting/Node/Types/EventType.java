package Radium.Engine.Scripting.Node.Types;

import Radium.Engine.Scripting.Node.Events.NodeEvent;

public class EventType extends NodeIoType {

    public EventType() {
        super("Event", new NodeEvent(""));
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("Event");
    }

}
