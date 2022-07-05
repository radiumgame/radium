package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Serializes components into json
 */
public class ClassTypeAdapter implements JsonSerializer<Class>, JsonDeserializer<Class> {

    
    public JsonElement serialize(Class src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("className", new JsonPrimitive(src.getCanonicalName()));

        return result;
    }

    
    public Class deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String type = object.get("className").getAsString();

        try {
            return Class.forName(type);
        }
        catch (Exception e) {
            Console.Error(e);
        }

        return null;
    }
}
