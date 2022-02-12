package Radium.Scripting;

import java.util.ArrayList;
import java.util.List;

public class ScriptingNode {

    public String name;
    public List<NodeInput> inputs = new ArrayList<>();
    public List<NodeInput> outputs = new ArrayList<>();
    public Runnable display = () -> {};
    public Runnable action = () -> {};

    public ScriptingNode() {
        Initialize();
    }

    private void Initialize() {
        inputs.add(NodeType.InputAction(this));
        outputs.add(NodeType.OutputAction(this));
    }

}
