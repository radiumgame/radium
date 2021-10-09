package Engine.Serialization.TypeAdapters;

import Editor.Console;
import Engine.Component;
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
            return context.deserialize(properties, Class.forName(type));
        }
        catch (Exception e) {
            Console.Error(e);
        }

        return null;
    }
}
