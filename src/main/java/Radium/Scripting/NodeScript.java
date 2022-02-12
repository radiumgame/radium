package Radium.Scripting;

import java.util.ArrayList;
import java.util.List;

public class NodeScript {

    public List<NodeScriptProperty> properties = new ArrayList<>();
    public List<ScriptingNode> nodes = new ArrayList<>();

    public NodeScript() {
        nodes.add(NodeType.Start());
        // nodes.add(NodeType.Update());
    }

    public NodeInput GetNodeInputByID(int id) {
        for (ScriptingNode node : nodes) {
            for (NodeInput input : node.inputs) {
                if (input.ID == id) {
                    return input;
                }
            }

            for (NodeInput output : node.outputs) {
                if (output.ID == id) {
                    return output;
                }
            }
        }

        return null;
    }

}
