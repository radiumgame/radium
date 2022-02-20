package Radium.Scripting;

import Radium.Math.Vector.Vector3;
import Radium.Time;
import RadiumEditor.Console;

public class Nodes {

    protected Nodes() {}

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
        node.nodeType = NodeType.Update;
        node.inputs.clear();

        return node;
    }

    public static ScriptingNode Update() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Update";
        node.nodeType = NodeType.Update;
        node.inputs.clear();

        return node;
    }

    public static ScriptingNode AddNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Add";
        node.nodeType = NodeType.Add;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Float.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Float.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode SubtractNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Subtract";
        node.nodeType = NodeType.Subtract;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Float.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Float.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode MultiplyNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Multiply";
        node.nodeType = NodeType.Multiply;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Float.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Float.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode DivideNode() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Divide";
        node.nodeType = NodeType.Divide;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput a = new NodeInput(node);
        a.name = "A";
        a.type = Float.class;

        NodeInput b = new NodeInput(node);
        b.name = "B";
        b.type = Float.class;

        NodeInput output = new NodeInput(node);
        output.name = "Output";
        output.type = Float.class;

        node.inputs.add(a);
        node.inputs.add(b);
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode DecomposeVector() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Decompose Vector";
        node.nodeType = NodeType.DecomposeVector;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput input = new NodeInput(node);
        input.name = "Vector3";
        input.type = Vector3.class;
        input.object = new Vector3(0, 0, 0);
        node.inputs.add(input);

        NodeInput x = new NodeInput(node);
        x.name = "X";
        x.type = Float.class;
        node.outputs.add(x);

        NodeInput y = new NodeInput(node);
        y.name = "Y";
        y.type = Float.class;
        node.outputs.add(y);

        NodeInput z = new NodeInput(node);
        z.name = "Z";
        z.type = Float.class;
        node.outputs.add(z);

        return node;
    }

    public static ScriptingNode ComposeVector() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Compose Vector";
        node.nodeType = NodeType.ComposeVector;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput x = new NodeInput(node);
        x.name = "X";
        x.type = Float.class;
        x.object = 0f;
        node.inputs.add(x);

        NodeInput y = new NodeInput(node);
        y.name = "Y";
        y.type = Float.class;
        y.object = 0f;
        node.inputs.add(y);

        NodeInput z = new NodeInput(node);
        z.name = "Z";
        z.type = Float.class;
        z.object = 0f;
        node.inputs.add(z);

        NodeInput output = new NodeInput(node);
        output.name = "Vector";
        output.type = Vector3.class;
        node.outputs.add(output);

        return node;
    }

    public static ScriptingNode Log() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Log";
        node.nodeType = NodeType.Log;

        NodeInput input = new NodeInput(node);
        input.name = "Message";
        input.type = Object.class;
        node.inputs.add(input);

        node.action = (script) -> Console.Log(input.object);

        return node;
    }

    public static ScriptingNode Time() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Time";
        node.nodeType = NodeType.Time;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput deltaTime = new NodeInput(node);
        deltaTime.name = "Delta Time";
        deltaTime.type = Float.class;
        deltaTime.object = Time.deltaTime;
        node.outputs.add(deltaTime);

        node.update = (script) -> {
            node.outputs.get(0).object = Time.deltaTime;
        };

        return node;
    }

    public static ScriptingNode Position() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Position";
        node.nodeType = NodeType.Position;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput pos = new NodeInput(node);
        pos.name = "Position";
        pos.type = Vector3.class;
        node.outputs.add(pos);

        return node;
    }

    public static ScriptingNode SetPosition() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Position";
        node.nodeType = NodeType.SetPosition;

        NodeInput pos = new NodeInput(node);
        pos.name = "Position";
        pos.type = Vector3.class;
        pos.object = Vector3.Zero();
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode Rotation() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Rotation";
        node.nodeType = NodeType.Rotation;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput pos = new NodeInput(node);
        pos.name = "Rotation";
        pos.type = Vector3.class;
        node.outputs.add(pos);

        return node;
    }

    public static ScriptingNode SetRotation() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Rotation";
        node.nodeType = NodeType.SetRotation;

        NodeInput pos = new NodeInput(node);
        pos.name = "Rotation";
        pos.type = Vector3.class;
        pos.object = Vector3.Zero();
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode Scale() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Scale";
        node.nodeType = NodeType.Scale;

        node.inputs.clear();
        node.outputs.clear();

        NodeInput pos = new NodeInput(node);
        pos.name = "Scale";
        pos.type = Vector3.class;
        node.outputs.add(pos);

        return node;
    }

    public static ScriptingNode SetScale() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Set Scale";
        node.nodeType = NodeType.SetScale;

        NodeInput pos = new NodeInput(node);
        pos.name = "Scale";
        pos.type = Vector3.class;
        pos.object = Vector3.Zero();
        node.inputs.add(pos);

        return node;
    }

    public static ScriptingNode Translate() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Translate";
        node.nodeType = NodeType.Translate;

        NodeInput translation = new NodeInput(node);
        translation.name = "Translation";
        translation.type = Vector3.class;
        node.inputs.add(translation);

        return node;
    }

    public static ScriptingNode Rotate() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Rotate";
        node.nodeType = NodeType.Rotate;

        NodeInput rotation = new NodeInput(node);
        rotation.name = "Rotation";
        rotation.type = Vector3.class;
        node.inputs.add(rotation);

        return node;
    }

    public static ScriptingNode Scaling() {
        ScriptingNode node = new ScriptingNode();
        node.name = "Scaling";
        node.nodeType = NodeType.Scaling;

        NodeInput scale = new NodeInput(node);
        scale.name = "Scale";
        scale.type = Vector3.class;
        node.inputs.add(scale);

        return node;
    }

}
