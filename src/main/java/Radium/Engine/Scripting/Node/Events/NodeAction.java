package Radium.Engine.Scripting.Node.Events;

import Radium.Engine.Scripting.Node.IO.NodeInput;
import Radium.Engine.Scripting.Node.IO.NodeOutput;
import Radium.Engine.Scripting.Node.Node;

import java.util.List;
import java.util.function.BiConsumer;

public class NodeAction {

    private Node node;
    private BiConsumer<List<NodeInput>, List<NodeOutput>> action;

    public NodeAction(Node node, BiConsumer<List<NodeInput>, List<NodeOutput>> action) {
        this.node = node;
        this.action = action;
    }

    public void Call() {
        action.accept(node.inputs, node.outputs);
    }

}
