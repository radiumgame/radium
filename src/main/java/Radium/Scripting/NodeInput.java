package Radium.Scripting;

import Radium.Math.Random;

import java.util.ArrayList;
import java.util.List;

public class NodeInput {

    public String name;
    public Object object;
    public Class type;
    public ScriptingNode node;
    public int ID = Random.RandomInt(1, 99999);

    public List<NodeInput> links = new ArrayList<>();

    public NodeInput(ScriptingNode node) {
        type = int.class;

        this.node = node;
    }

    public void Linked(NodeInput other) {
        other.object = object;
    }

}
