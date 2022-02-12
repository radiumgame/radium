package Radium.Scripting;

import Radium.Time;
import RadiumEditor.Console;

public class NodeType {

    protected NodeType() {}

    public static NodeInput InputAction(ScriptingNode node) {
        NodeInput input = new NodeInput(node);
        input.name = "Trigger In";
        input.type = NodeTrigger.class;

        return input;
    }

    public static NodeInput OutputAction(ScriptingNode node) {
        NodeInput input = new NodeInput(node);
        input.name = "Trigger Out";
        input.type = NodeTrigger.class;

        return input;
    }

    public static ScriptingNode Start() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Start";
        node.inputs.clear();

        return node;
    }

    public static ScriptingNode Update() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Update";
        node.inputs.clear();

        return node;
    }

    public static ScriptingNode AddNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Add";

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = float.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = float.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        node.action = () -> output.object = (float)a.object + (float)b.object;

        return node;
    }

    public static ScriptingNode Log() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Log";

        NodeInput input = new NodeInput(node);
        input.name = "Message";
        input.type = Object.class;
        node.inputs.add(input);

        node.action = () -> Console.Log(node.inputs.get(1).object);

        return node;
    }

    public static ScriptingNode Time() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Time";

        node.inputs.clear();
        node.outputs.clear();

        NodeInput deltaTime = new NodeInput(node);
        deltaTime.name = "Delta Time";
        deltaTime.type = Float.class;
        deltaTime.object = Time.deltaTime;
        node.outputs.add(deltaTime);

        node.update = () -> {
            node.outputs.get(0).object = Time.deltaTime;
        };

        return node;
    }

}
