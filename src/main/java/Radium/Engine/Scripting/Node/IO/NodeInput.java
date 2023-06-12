package Radium.Engine.Scripting.Node.IO;

import Radium.Engine.Scripting.Node.Events.Link;
import Radium.Engine.Scripting.Node.Types.NodeIoType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class NodeInput extends NodeIO {

    public Link link;
    @JsonIgnore
    public int icon = -1;

    public NodeInput(String name, NodeIoType type) {
        this.name = name;
        this.type = type;
        this.isInput = true;

        this.id = NextIoID;
        NextIoID++;

        Create();
    }

    public NodeInput(String name, boolean showName, NodeIoType type) {
        this.name = name;
        this.showName = showName;
        this.type = type;
        this.isInput = true;

        this.id = NextIoID;
        NextIoID++;

        Create();
    }

    public NodeInput(String name, boolean showName, int icon, NodeIoType type) {
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.showName = showName;
        this.isInput = true;

        this.id = NextIoID;
        NextIoID++;

        Create();
    }

}
