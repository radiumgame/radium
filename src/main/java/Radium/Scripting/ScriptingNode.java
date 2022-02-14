package Radium.Scripting;

import Radium.Math.Random;
import Radium.Objects.GameObject;
import RadiumEditor.Console;
import imgui.ImVec2;

import java.util.ArrayList;
import java.util.List;

public class ScriptingNode {

    public String name;
    public List<NodeInput> inputs = new ArrayList<>();
    public List<NodeInput> outputs = new ArrayList<>();
    public transient Runnable action = () -> {};
    public transient Runnable update = () -> {};

    public int ID = Random.RandomInt(1, 99999);
    public NodeType nodeType = NodeType.Start;

    public ImVec2 position = new ImVec2(0, 0);

    public transient GameObject gameObject;

    public ScriptingNode() {
        Initialize();
    }

    private void Initialize() {
        inputs.add(Nodes.InputAction(this));
        outputs.add(Nodes.OutputAction(this));
    }

    public void Update() {
        update.run();

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

}
