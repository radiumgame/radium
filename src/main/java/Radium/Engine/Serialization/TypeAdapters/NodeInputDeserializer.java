package Radium.Engine.Serialization.TypeAdapters;

import Radium.Engine.Scripting.Nodes.NodeInput;
import Radium.Editor.Console;
import Radium.Engine.Serialization.Serializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NodeInputDeserializer extends StdDeserializer<NodeInput> {

    public NodeInputDeserializer() {
        super(NodeInput.class);
    }
    
    public NodeInput deserialize(JsonParser p, DeserializationContext context) {
        try {
            TreeNode node = p.readValueAsTree();
            String name = Serializer.ReadString(node.get("name"));
            int ID = node.get("ID").traverse(p.getCodec()).getValueAsInt();
            Class type = node.get("type").traverse(p.getCodec()).readValueAs(Class.class);
            String className = type.getName();
            Object obj = node.get("object").traverse(p.getCodec()).readValueAs(Class.forName(className));

            NodeInput input = new NodeInput(null);
            input.name = name;
            input.ID = ID;
            input.links = new ArrayList<>();
            input.type = Class.forName(className);
            input.object = obj;

            return input;
        } catch (Exception e) {
            Console.Error(e);
        }

        return null;
    }

}
