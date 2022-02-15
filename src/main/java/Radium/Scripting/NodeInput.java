package Radium.Scripting;

import Radium.Math.Random;
import RadiumEditor.Console;

import java.util.ArrayList;
import java.util.List;

public class NodeInput {

    public String name;
    public Object object;
    public Class type;
    public transient ScriptingNode node;
    public int ID = Random.RandomInt(1, 999999);

    public transient List<NodeInput> links = new ArrayList<>();

    public NodeInput(ScriptingNode node) {
        type = int.class;

        this.node = node;
    }

    public void Link(NodeInput other) {
        other.object = object;
    }

    public void UpdateLinks() {
        for (NodeInput link : links) {
            link.object = object;
        }
    }

}
