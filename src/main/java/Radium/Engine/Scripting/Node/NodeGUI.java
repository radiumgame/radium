package Radium.Engine.Scripting.Node;

import Radium.Engine.Scripting.Node.IO.NodeInput;
import Radium.Engine.Scripting.Node.IO.NodeOutput;

import java.util.List;
import java.util.function.BiConsumer;

public class NodeGUI {

    private Node node;
    private BiConsumer<List<NodeInput>, List<NodeOutput>> guiRender;

    public NodeGUI(BiConsumer<List<NodeInput>, List<NodeOutput>> guiRender, Node node) {
        this.guiRender = guiRender;
        this.node = node;
    }

    public void Call() {
        guiRender.accept(node.inputs, node.outputs);
    }

}
