package Radium.Engine.Scripting.Nodes;

import Radium.Engine.Color.Color;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;

import java.util.ArrayList;
import java.util.List;

public class NodeInput {

    public String name = "Empty";
    public Object object;
    public Class type;
    public transient ScriptingNode node;
    public int ID = Nodes.IDGen.NewID();

    public transient List<NodeInput> links = new ArrayList<>();

    public NodeInput(ScriptingNode node) {
        type = int.class;

        this.node = node;
    }

    public void Link(NodeInput other) {
        other.object = object;
    }

    public void Update() {
        if (object == null) {
            object = Value();
        }

        if (object.getClass() == Double.class) {
            Double d = (Double)object;
            object = d.floatValue();
        }

        UpdateLinks();
    }

    public void UpdateLinks() {
        for (NodeInput link : links) {
            link.object = object;
        }
    }

    public Object Value() {
        if (object != null) return object;

        if (Integer.class.equals(type)) {
            return 0;
        } else if (Float.class.equals(type)) {
            return 0f;
        } else if (Boolean.class.equals(type)) {
            return false;
        } else if (String.class.equals(type)) {
            return "";
        } else if (Vector2.class.equals(type)) {
            return Vector2.Zero();
        } else if (Vector3.class.equals(type)) {
            return Vector3.Zero();
        } else if (Color.class.equals(type)) {
            return new Color(1f, 1f, 1f);
        }

        return 0f;
    }

}
