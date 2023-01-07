package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.PostProcessing.Effects.Tint;
import Radium.Engine.PostProcessing.PostProcessingEffect;
import Radium.Engine.Serialization.Serializer;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class PostProcessingEffectDeserializer extends StdDeserializer<PostProcessingEffect> {

    public PostProcessingEffectDeserializer() {
        super(PostProcessingEffect.class);
    }

    @Override
    public PostProcessingEffect deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        TreeNode node = p.readValueAsTree();
        String type = Serializer.ReadString(node.get("type"));
        String props = node.get("properties").toString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());

        try {
            return (PostProcessingEffect)mapper.readValue(props, Class.forName(type));
        } catch (Exception e) {
            Console.Error(e);
            return new Tint();
        }
    }
}
