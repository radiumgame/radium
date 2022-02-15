package Radium.Scripting;

import Radium.Math.Random;
import Radium.Objects.GameObject;
import RadiumEditor.Console;
import imgui.ImVec2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScriptingNode {

    public String name;
    public List<NodeInput> inputs = new ArrayList<>();
    public List<NodeInput> outputs = new ArrayList<>();
    public transient Consumer<NodeScript> action = (script) -> {};
    public transient Consumer<NodeScript> start = (script) -> {};
    public transient Consumer<NodeScript> update = (script) -> {};

    public int ID = Random.RandomInt(1, 99999);
    public NodeType nodeType = NodeType.Start;

    public ImVec2 position = new ImVec2(0, 0);

    public transient GameObject gameObject = new GameObject(false);

    public ScriptingNode() {
        Initialize();
    }

    private void Initialize() {
        inputs.add(Nodes.InputAction(this));
        outputs.add(Nodes.OutputAction(this));
    }

    public void Start(NodeScript script) {
        start.accept(script);

        for (NodeInput output : outputs) {
            output.UpdateLinks();
        }
    }

    public void Update(NodeScript script) {
        update.accept(script);

        for (NodeInput output : outputs) {
            output.UpdateLinks();
        }
    }

    public NodeInput GetTriggerOutput() {
        for (NodeInput output : outputs) {
            if (output.type == NodeTrigger.class) {
                return output;
            }
        }

        return null;
    }

    public NodeInput GetInput(String name) {
        for (NodeInput input : inputs) {
            if (input.name == name) {
                return input;
            }
        }

        return null;
    }

    public NodeInput GetOutput(String name) {
        for (NodeInput output : outputs) {
            if (output.name == name) {
                return output;
            }
        }

        return null;
    }

}
