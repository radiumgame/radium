package Radium.Engine.Scripting.Node.Types;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class NodeIoType {

    public String name;
    @JsonIgnore
    public Object defaultValue;

    public NodeIoType(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public abstract boolean CanLink(NodeIoType other);

}
