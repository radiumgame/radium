package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.Component;
import Radium.Engine.Components.Graphics.MeshRenderer;
import Radium.Engine.Components.Rendering.PostProcessing;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.PostProcessing.CustomPostProcessingEffect;
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
import java.io.File;

public class ComponentDeserializer extends StdDeserializer<Component> {

    public ComponentDeserializer() {
        super(Component.class);
    }

    @Override
    public Component deserialize(JsonParser p, DeserializationContext context) throws IOException, JacksonException {
        try {
            TreeNode node = p.readValueAsTree();
            String type = Serializer.ReadString(node.get("type"));
            String props = node.get("properties").toString();
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(String.class, new StringDeserializer());
            module.addDeserializer(File.class, new FileDeserializer());
            module.addDeserializer(Mesh.class, new MeshDeserializer());
            mapper.registerModule(module);
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());

            Component comp = (Component)mapper.readValue(props, Class.forName(type));
            if (comp.getClass() == PostProcessing.class) {
                AddPostProcessingEffect((PostProcessing)comp);
            }

            return comp;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void AddPostProcessingEffect(PostProcessing comp) {
        for (PostProcessingEffect effect : comp.effects) {
            Radium.Engine.PostProcessing.PostProcessing.AddEffect(effect);
        }

        Radium.Engine.PostProcessing.PostProcessing.customEffects.addAll(comp.custom);
    }

}
