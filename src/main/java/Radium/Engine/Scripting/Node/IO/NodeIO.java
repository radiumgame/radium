package Radium.Engine.Scripting.Node.IO;

import Radium.Editor.Icons;
import Radium.Engine.Scripting.Node.Events.NodeEvent;
import Radium.Engine.Scripting.Node.Types.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

public class NodeIO {

    protected static int NextIoID = 0;
    public static final HashMap<Integer, NodeIO> io = new HashMap<>();

    public NodeIoType type;
    public Object value;
    public String name = "";
    public boolean showName = true;
    @JsonIgnore
    public int id;
    @JsonIgnore
    public int parentId;

    public boolean isInput = false;
    public boolean isOutput = false;

    public void SetBaseObject() {
        value = type.defaultValue;
    }

    protected void Create() {
        io.put(id, this);
        SetBaseObject();
    }

    public void SetParent(int id) {
        this.parentId = id;
    }

    public static NodeIO GetIO(int id) {
        return io.get(id);
    }

    public static NodeInput EventInput() {
        return new NodeInput("Event", false, Icons.GetIcon("node_event"), new EventType());
    }

    public static NodeOutput EventOutput() {
        return new NodeOutput("Event", false, Icons.GetIcon("node_event"), new EventType());
    }

    public static NodeInput ObjectInput(String name) {
        return new NodeInput(name, true, new ObjectType());
    }

    public static NodeOutput ObjectOutput(String name) {
        return new NodeOutput(name, true, new ObjectType());
    }

    public static NodeInput StringInput(String name) {
        return new NodeInput(name, true, new StringType());
    }

    public static NodeOutput StringOutput(String name) {
        return new NodeOutput(name, true, new StringType());
    }

    public static NodeInput IntInput(String name) {
        return new NodeInput(name, true, new IntType());
    }

    public static NodeOutput IntOutput(String name) {
        return new NodeOutput(name, true, new IntType());
    }

    public static NodeInput FloatInput(String name) {
        return new NodeInput(name, true, new FloatType());
    }

    public static NodeOutput FloatOutput(String name) {
        return new NodeOutput(name, true, new FloatType());
    }

    public static NodeInput Vector2Input(String name) {
        return new NodeInput(name, true, new Vector2Type());
    }

    public static NodeOutput Vector2Output(String name) {
        return new NodeOutput(name, true, new Vector2Type());
    }

    public static NodeInput Vector3Input(String name) {
        return new NodeInput(name, true, new Vector3Type());
    }

    public static NodeOutput Vector3Output(String name) {
        return new NodeOutput(name, true, new Vector3Type());
    }

    public static NodeInput GameObjectInput(String name) {
        return new NodeInput(name, true, new GameObjectType());
    }

    public static NodeOutput GameObjectOutput(String name) {
        return new NodeOutput(name, true, new GameObjectType());
    }

    public static NodeInput NumberOrVectorInput(String name) {
        return new NodeInput(name, true, new NumberAndVectorType());
    }

    public static NodeOutput NumberOrVectorOutput(String name) {
        return new NodeOutput(name, true, new NumberAndVectorType());
    }

}
