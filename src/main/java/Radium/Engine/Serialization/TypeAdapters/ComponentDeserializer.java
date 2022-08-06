package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.Component;
import Radium.Engine.Components.Graphics.MeshRenderer;
import Radium.Engine.Graphics.Mesh;
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
            module.addDeserializer(Mesh.class, new MeshDeserializer());
            mapper.registerModule(module);
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            Component comp = (Component)mapper.readValue(props, Class.forName(type));

            /*
            if (comp.getClass() == PostProcessing.class) {
                PostProcessing postp = (PostProcessing)comp;
                PostProcessingEffect[] effects = p.readValueAs(List.class);
                JsonArray customs = object.get("customEffects").getAsJsonArray();

                for (JsonElement effect : effects) {
                    PostProcessingEffect e = context.deserialize(effect, PostProcessingEffect.class);
                    PostProcessingEffect newEffect = context.deserialize(effect, e.effectType);

                    Radium.Engine.PostProcessing.PostProcessing.AddEffect(newEffect);
                }
                for (JsonElement custom : customs) {
                    CustomPostProcessingEffect effect = context.deserialize(custom, CustomPostProcessingEffect.class);

                    Radium.Engine.PostProcessing.PostProcessing.customEffects.add(effect);
                }

                return postp;
            }*/

            return comp;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
