package Radium.Engine.Scripting.Node;

import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Scripting.Node.Events.Link;
import Radium.Engine.Scripting.Node.Events.NodeAction;
import Radium.Engine.Scripting.Node.IO.NodeIO;
import Radium.Engine.Scripting.Node.IO.NodeInput;
import Radium.Engine.Scripting.Node.IO.NodeOutput;
import imgui.extension.imnodes.ImNodes;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Node {

    private static int NextNodeID;
    private static final HashMap<Integer, Node> nodes = new HashMap<>();

    public List<NodeInput> inputs = new LinkedList<>();
    public List<NodeOutput> outputs = new LinkedList<>();
    public Vector2 position = new Vector2(0, 0);
    private NodeAction action = new NodeAction(this, (inputs, outputs) -> {});
    public NodeGUI gui = new NodeGUI((inputs, outputs) -> {}, this);
    private BiConsumer<Node, NodeIO> onLink = ((other, io) -> {});

    public boolean isProperty = false;
    public String property = "";


    public int Icon = -1;
    private HashMap<String, Object> temp = new HashMap<>();
    private NodeGraph graph;

    public boolean NeedSetPosition = false;

    public int id;
    public String name;

    public String uuid;

    public Node(String name) {
        id = NextNodeID;
        NextNodeID++;
        this.name = name;

        uuid = UUID.randomUUID().toString();

        nodes.put(id, this);
    }

    public Node SetPosition(float x, float y) {
        position.x = x;
        position.y = y;
        NeedSetPosition = true;

        return this;
    }

    public void ChangeOutput(int index, NodeOutput newOutput) {
        NodeOutput oldOutput = outputs.get(index);
        NodeIO.io.remove(oldOutput.id);
        outputs.set(index, newOutput);
    }

    public Node SetAction(BiConsumer<List<NodeInput>, List<NodeOutput>> action) {
        this.action = new NodeAction(this, action);
        return this;
    }

    public Node SetGUI(BiConsumer<List<NodeInput>, List<NodeOutput>> gui) {
        this.gui = new NodeGUI(gui, this);
        return this;
    }

    public void RunNode() {
        action.Call();

        List<Link> links = GetEventLinks();
        for (Link link : links) {
            Node node = Node.GetNode(link.endNode);
            node.RunNode();
        }
    }

    private List<Link> GetEventLinks() {
        List<Link> res = new LinkedList<>();
        for (NodeOutput output : outputs) {
            if (!output.type.name.equals("Event")) continue;
            res.addAll(output.links);
        }

        return res;
    }

    public void OnLink(Node other, NodeIO io) {
        if (io.isOutput) {
            UpdateValue((NodeOutput) io);
        }

        onLink.accept(other, io);
    }

    public Node OnLink(BiConsumer<Node, NodeIO> onLink) {
        this.onLink = onLink;
        return this;
    }

    public void UpdatePosition() {
        ImNodes.setNodeEditorSpacePos(id, position.x, position.y);
    }

    public void SetGraph(NodeGraph graph) {
        this.graph = graph;
    }

    public Node AddInput(NodeInput input) {
        inputs.add(input);
        input.SetParent(id);

        return this;
    }

    public Node AddOutput(NodeOutput output) {
        outputs.add(output);
        output.SetParent(id);

        return this;
    }

    public NodeInput GetInput(String name) {
        for (NodeInput input : inputs) {
            if (input.name.equals(name)) return input;
        }

        return null;
    }

    public NodeOutput GetOutput(String name) {
        for (NodeOutput output : outputs) {
            if (output.name.equals(name)) return output;
        }

        return null;
    }

    public GameObject GetGameObject() {
        return graph.gameObject;
    }

    public Node SetIcon(String path) {
        Icon = new Texture(path, true).GetTextureID();
        return this;
    }

    public void AddTempVariable(String name, Object defaultValue) {
        temp.put(name, defaultValue);
    }

    public void SetTempVariable(String name, Object value) {
        temp.replace(name, value);
    }

    public Object GetTempVariable(String name) {
        return temp.get(name);
    }

    public void UpdateValue(NodeOutput output) {
        for (Link link : output.links) {
            NodeInput nodeIo = (NodeInput) NodeIO.GetIO(link.endIo);
            nodeIo.value = output.value;

            UpdateOutputs(Node.GetNode(link.endNode));
        }
    }

    private void UpdateOutputs(Node node) {
        for (NodeOutput output : node.outputs) {
            for (Link link : output.links) {
                NodeInput nodeIo = (NodeInput) NodeIO.GetIO(link.endIo);
                nodeIo.value = output.value;
                UpdateOutputs(Node.GetNode(link.endNode));
            }
        }
    }

    public static Node GetNode(int id) {
        return nodes.get(id);
    }

    public static void DestroyNode(int id) {
        nodes.remove(id);
    }

}
