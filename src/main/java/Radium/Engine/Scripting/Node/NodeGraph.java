package Radium.Engine.Scripting.Node;

import Radium.Editor.Console;
import Radium.Editor.Files.Parser;
import Radium.Editor.Icons;
import Radium.Editor.NodeScripting.NodeScripting;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Scripting.Node.Events.Link;
import Radium.Engine.Scripting.Node.Properties.Property;
import Radium.Engine.Serialization.TypeAdapters.NodeGraphDeserializer;
import Radium.Engine.Serialization.TypeAdapters.NodeGraphSerializer;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.Util.FileUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class NodeGraph {

    public List<Node> nodes = new ArrayList<>();
    public List<Link> links = new ArrayList<>();
    private final HashMap<String, Node> nodesByUUID = new HashMap<>();

    public String path = null;

    public GameObject gameObject;

    private List<Node> startNodes = new LinkedList<>();
    private List<Node> updateNodes = new LinkedList<>();

    private HashMap<String, Property> properties = new HashMap<>();

    public Node CreateNode(Node node) {
        nodes.add(node);
        nodesByUUID.put(node.uuid, node);
        node.SetGraph(this);

        return node;
    }

    public void DestroyNode(Node node) {
        nodes.remove(node);
    }

    public void GetEvents() {
        startNodes = GetNodes("Start");
        updateNodes = GetNodes("Update");
    }

    public void Start() {
        startNodes.forEach(Node::RunNode);
    }

    public void Update() {
        updateNodes.forEach(Node::RunNode);
    }

    private List<Node> GetNodes(String name) {
        List<Node> res = new ArrayList<>();
        for (Node node : nodes) {
            if (node.name.equals(name)) res.add(node);
        }

        return res;
    }

    public List<Node> GetNodes() {
        return nodes;
    }

    private int NumOfProperties = 0;
    public Property AddProperty(String name) {
        String newName = name + (NumOfProperties > 0 ? NumOfProperties : "");
        Property newProperty = new Property(newName);
        newProperty.graph = this;
        properties.put(newName, newProperty);
        NumOfProperties++;
        return newProperty;
    }

    public void DestroyProperty(Property property) {
        properties.remove(property.name);
        NodeScripting.UpdateProperties();
    }

    public void ChangeNameOfProperty(String oldName, String newName, Property property) {
        properties.remove(oldName);
        properties.put(newName, property);
    }

    public Property AddProperty(Property property) {
        properties.put(property.name, property);
        NumOfProperties++;
        return property;
    }

    public Property GetProperty(String name) {
        return properties.get(name);
    }

    public Collection<Property> GetProperties() {
        return properties.values();
    }

    public void Save(String file) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(NodeGraph.class, new NodeGraphSerializer());
        mapper.registerModule(module);

        try {
            if (!Files.exists(Paths.get(file))) {
                FileUtility.Create(file);
                Parser.UpdateGraphs();
            }

            String json = mapper.writeValueAsString(this);
            FileUtility.Write(new File(file), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static NodeGraph Load(String path, GameObject gameObject) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(NodeGraph.class, new NodeGraphDeserializer());
        mapper.registerModule(module);

        try {
            String json = FileUtility.ReadFile(new File(path));
            NodeGraph graph = mapper.readValue(json, NodeGraph.class);
            graph.path = path;
            graph.gameObject = gameObject;
            return graph;
        } catch (Exception e) {
            Console.Error(e);
            return BasicGraph();
        }
    }

    public static NodeGraph BasicGraph() {
        NodeGraph graph = new NodeGraph();

        graph.CreateNode(Nodes.Start().SetPosition(300, 100));
        graph.CreateNode(Nodes.Update().SetPosition(300, 180));

        return graph;
    }

}
