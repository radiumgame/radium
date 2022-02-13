package Radium.Scripting;

import Radium.Math.Vector.Vector2;
import RadiumEditor.Console;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScriptingNode {

    public String name;
    public List<NodeInput> inputs = new ArrayList<>();
    public List<NodeInput> outputs = new ArrayList<>();
    public Runnable display = () -> {};
    public Runnable action = () -> {};
    public Runnable update = () -> {};

    public ScriptingNode() {
        Initialize();
    }

    private void Initialize() {
        inputs.add(NodeType.InputAction(this));
        outputs.add(NodeType.OutputAction(this));
    }

    public void Update() {
        update.run();

        for (NodeInput output : outputs) {
            output.UpdateLinks();
        }
    }

}
