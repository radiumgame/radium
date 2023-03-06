package Radium.Engine.Serialization.TypeAdapters;

import Radium.Engine.PostProcessing.PostProcessingEffect;
import Radium.Engine.Scripting.Node.Events.Link;
import Radium.Engine.Scripting.Node.IO.NodeIO;
import Radium.Engine.Scripting.Node.IO.NodeInput;
import Radium.Engine.Scripting.Node.IO.NodeOutput;
import Radium.Engine.Scripting.Node.Node;
import Radium.Engine.Scripting.Node.NodeGraph;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import imgui.ImVec2;
import imgui.extension.imnodes.ImNodes;

import java.io.IOException;

public class NodeGraphSerializer extends StdSerializer<NodeGraph> {

    public NodeGraphSerializer() {
        super(NodeGraph.class);
    }

    @Override
    public void serialize(NodeGraph value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeArrayFieldStart("nodes");
        for (Node node : value.nodes) {
            gen.writeStartObject();

            gen.writeStringField("uuid", node.uuid);
            gen.writeStringField("nodeName", node.name);

            ImVec2 position = new ImVec2();
            ImNodes.getNodeEditorSpacePos(node.id, position);
            gen.writeNumberField("posX", position.x);
            gen.writeNumberField("posY", position.y);

            gen.writeArrayFieldStart("inputs");
            for (NodeInput input : node.inputs) {
                gen.writeStartObject();
                gen.writeObjectField("value", input.value);
                gen.writeStringField("type", input.type.name);
                gen.writeEndObject();
            }
            gen.writeEndArray();

            gen.writeArrayFieldStart("outputs");
            for (NodeOutput output : node.outputs) {
                gen.writeStartObject();
                gen.writeObjectField("value", output.value);
                gen.writeStringField("type", output.type.name);
                gen.writeEndObject();
            }
            gen.writeEndArray();

            gen.writeEndObject();
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("links");
        for (Link link : value.links) {
            gen.writeStartObject();

            Node start = Node.GetNode(link.startNode);
            NodeOutput startOutput = (NodeOutput) NodeIO.GetIO(link.startIo);
            Node end = Node.GetNode(link.endNode);
            NodeInput endInput = (NodeInput) NodeIO.GetIO(link.endIo);

            gen.writeStringField("startNode", start.uuid);
            gen.writeStringField("startIoName", startOutput.name);
            gen.writeStringField("endNode", end.uuid);
            gen.writeStringField("endIoName", endInput.name);

            gen.writeEndObject();
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}

