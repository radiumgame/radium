package Radium.Serialization.TypeAdapters;

import RadiumEditor.Console;
import Radium.Component;
import Radium.Components.Graphics.MeshFilter;
import Radium.Graphics.Material;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ComponentTypeAdapter implements JsonSerializer<Component>, JsonDeserializer<Component> {

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", context.serialize(src, src.getClass()));

        return result;
    }

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String type = object.get("type").getAsString();
        JsonElement properties = object.get("properties");

        try {
            Component comp = context.deserialize(properties, Class.forName(type));

            return comp;
        }
        catch (Exception e) {
            Console.Error(e);
        }

        return null;
    }
}
