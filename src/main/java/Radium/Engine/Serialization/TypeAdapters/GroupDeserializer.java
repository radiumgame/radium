package Radium.Engine.Serialization.TypeAdapters;

import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Objects.Groups.Group;
import Radium.Engine.Serialization.Serializer;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class GroupDeserializer extends StdDeserializer<Group> {

    public GroupDeserializer() {
        super(Group.class);
    }

    @Override
    public Group deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        TreeNode node = p.readValueAsTree();
        String name = Serializer.ReadString(node.get("name"));
        return Group.CreateGroup(name);
    }

}
