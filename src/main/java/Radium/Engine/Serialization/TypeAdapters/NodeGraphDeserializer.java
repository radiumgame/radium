package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.Component;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Scripting.Node.Events.Link;
import Radium.Engine.Scripting.Node.Events.NodeEvent;
import Radium.Engine.Scripting.Node.IO.NodeInput;
import Radium.Engine.Scripting.Node.IO.NodeOutput;
import Radium.Engine.Scripting.Node.Node;
import Radium.Engine.Scripting.Node.NodeGraph;
import Radium.Engine.Scripting.Node.Nodes;
import Radium.Engine.Scripting.Node.Properties.Property;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class NodeGraphDeserializer extends StdDeserializer<NodeGraph> {

    public NodeGraphDeserializer() {
        super(NodeGraph.class);
    }

    @Override
    public NodeGraph deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        try {
            NodeGraph graph = new NodeGraph();

            TreeNode graphBase = jsonParser.readValueAsTree();
            TreeNode properties = graphBase.get("properties");
            TreeNode nodes = graphBase.get("nodes");
            TreeNode links = graphBase.get("links");

            PropertyJSON[] propertiesArr = properties.traverse(jsonParser.getCodec()).readValueAs(PropertyJSON[].class);
            NodeJSON[] nodesArr = nodes.traverse(jsonParser.getCodec()).readValueAs(NodeJSON[].class);
            LinkJSON[] linksArr = links.traverse(jsonParser.getCodec()).readValueAs(LinkJSON[].class);

            for (PropertyJSON property : propertiesArr) {
                Property newProperty = new Property(property.name);
                newProperty.type = Property.availableProperties.get(property.typeIndex);
                newProperty.value = CheckType(property.value, newProperty.type.name);

                graph.AddProperty(newProperty);
            }

            HashMap<String, Node> nodeMap = new HashMap<>();
            for (NodeJSON node : nodesArr) {
                if (!node.isProperty) {
                    Node newNode = (Node) Nodes.class.getMethod(node.nodeName.replaceAll(" ", "")).invoke(null);
                    newNode.uuid = node.uuid;
                    newNode.SetPosition(node.posX, node.posY);
                    nodeMap.put(node.uuid, newNode);
                    graph.CreateNode(newNode);

                    ArrayList<Object> inputValues = (ArrayList<Object>) node.inputs;
                    int i = 0;
                    for (NodeInput input : newNode.inputs) {
                        LinkedHashMap<String, Object> val = (LinkedHashMap) inputValues.get(i);
                        input.value = CheckType(val.get("value"), (String) val.get("type"));
                        i++;
                    }

                    ArrayList<Object> outputValues = (ArrayList<Object>) node.outputs;
                    i = 0;
                    for (NodeOutput output : newNode.outputs) {
                        LinkedHashMap<String, Object> val = (LinkedHashMap) outputValues.get(i);
                        output.value = CheckType(val.get("value"), (String) val.get("type"));
                        i++;
                    }
                } else {
                    Node newNode = graph.GetProperty(node.property).CreateNode();
                    newNode.uuid = node.uuid;
                    newNode.SetPosition(node.posX, node.posY);
                    nodeMap.put(node.uuid, newNode);

                    graph.CreateNode(newNode);
                }
            }

            for (LinkJSON link : linksArr) {
                Node startNode = nodeMap.get(link.startNode);
                Node endNode = nodeMap.get(link.endNode);

                NodeOutput startIo = startNode.GetOutput(link.startIoName);
                NodeInput endIo = endNode.GetInput(link.endIoName);

                startNode.OnLink(endNode, startIo);
                endNode.OnLink(startNode, endIo);

                Link newLink = new Link(startNode.id, endNode.id, startIo.id, endIo.id, graph);
                startIo.links.add(newLink);
                endIo.link = newLink;
            }

            for (Node node : graph.nodes) {
                for (NodeOutput output : node.outputs) {
                    node.UpdateValue(output);
                }
            }

            return graph;
        } catch (Exception e) {
            Console.Error(e);
            return NodeGraph.BasicGraph();
        }
    }

    private static Object CheckType(Object obj, String type) {
        if (obj.getClass().equals(Double.class)) {
            obj = ((Double)obj).floatValue();
        } else if (obj.getClass().equals(LinkedHashMap.class)) {
            if (type.equals("Event")) {
                obj = new NodeEvent((boolean)((LinkedHashMap<?, ?>) obj).get("enabled"));
            } else if (type.equals("Vector2")) {
                LinkedHashMap data = (LinkedHashMap)obj;
                float x = ((Double)data.get("x")).floatValue();
                float y = ((Double)data.get("y")).floatValue();
                obj = new Vector2(x, y);
            } else if (type.equals("Vector3")) {
                LinkedHashMap data = (LinkedHashMap)obj;
                float x = ((Double)data.get("x")).floatValue();
                float y = ((Double)data.get("y")).floatValue();
                float z = ((Double)data.get("z")).floatValue();
                obj = new Vector3(x, y, z);
            }
        }

        return obj;
    }

    private static class PropertyJSON {

        public String name;
        public int typeIndex;
        public Object value;

    }

    private static class NodeJSON {

        public String uuid;
        public String nodeName;
        public float posX;
        public float posY;
        public boolean isProperty;
        public String property;
        public Object inputs;
        public Object outputs;

        public NodeJSON() {}

    }

    private static class LinkJSON {

        public String startNode;
        public String startIoName;
        public String endNode;
        public String endIoName;

    }

}
