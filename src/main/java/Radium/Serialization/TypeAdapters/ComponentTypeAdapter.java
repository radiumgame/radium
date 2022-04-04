package Radium.Serialization.TypeAdapters;

import Radium.Components.Rendering.PostProcessing;
import Radium.Components.Scripting.PythonScripting;
import Radium.PostProcessing.CustomPostProcessingEffect;
import Radium.PostProcessing.PostProcessingEffect;
import Radium.Scripting.Python.PythonScript;
import RadiumEditor.Console;
import Radium.Component;
import Radium.Components.Graphics.MeshFilter;
import Radium.Graphics.Material;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Serializes components into json
 */
public class ComponentTypeAdapter implements JsonSerializer<Component>, JsonDeserializer<Component> {

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", context.serialize(src, src.getClass()));

        if (src.getClass() == PostProcessing.class) {
            result.add("effects", context.serialize(((PostProcessing)src).effects));
            result.add("customEffects", context.serialize(((PostProcessing)src).custom));
        }

        return result;
    }

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String type = object.get("type").getAsString();
        JsonElement properties = object.get("properties");

        try {
            Component comp = context.deserialize(properties, Class.forName(type));

            if (comp.getClass() == PostProcessing.class) {
                PostProcessing postp = (PostProcessing)comp;
                JsonArray effects = object.get("effects").getAsJsonArray();
                JsonArray customs = object.get("customEffects").getAsJsonArray();

                for (JsonElement effect : effects) {
                    PostProcessingEffect e = context.deserialize(effect, PostProcessingEffect.class);
                    PostProcessingEffect newEffect = context.deserialize(effect, e.effectType);

                    Radium.PostProcessing.PostProcessing.AddEffect(newEffect);
                }
                for (JsonElement custom : customs) {
                    CustomPostProcessingEffect effect = context.deserialize(custom, CustomPostProcessingEffect.class);

                    Radium.PostProcessing.PostProcessing.customEffects.add(effect);
                }

                return postp;
            }

            return comp;
        }
        catch (Exception e) {
            Console.Error(e);
        }

        return null;
    }
}
