package Radium.Engine.Scripting.Node.IO;

import Radium.Engine.Scripting.Node.Events.Link;
import Radium.Engine.Scripting.Node.Types.NodeIoType;

import java.util.LinkedList;
import java.util.List;

public class NodeOutput extends NodeIO {

    public List<Link> links = new LinkedList<>();
    public int icon = -1;

    public NodeOutput(String name, NodeIoType type) {
        this.name = name;
        this.type = type;
        this.isOutput = true;

        this.id = NextIoID;
        NextIoID++;

        Create();
    }

    public NodeOutput(String name, boolean showName, NodeIoType type) {
        this.name = name;
        this.showName = showName;
        this.type = type;
        this.isOutput = true;

        this.id = NextIoID;
        NextIoID++;

        Create();
    }

    public NodeOutput(String name, boolean showName, int icon, NodeIoType type) {
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.showName = showName;
        this.isOutput = true;

        this.id = NextIoID;
        NextIoID++;

        Create();
    }

}
