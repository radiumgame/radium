package Radium.Engine.Serialization.TypeAdapters;

import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Vertex;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class MeshDeserializer extends StdDeserializer<Mesh> {

    public MeshDeserializer() {
        super(Mesh.class);
    }

    @Override
    public Mesh deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        TreeNode node = p.readValueAsTree();
        Vertex[] vertices = node.get("vertices").traverse(p.getCodec()).readValueAs(Vertex[].class);
        int[] indices = node.get("indices").traverse(p.getCodec()).readValueAs(int[].class);
        return new Mesh(vertices, indices);
    }
}
