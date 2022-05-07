package Radium.Serialization.TypeAdapters;

import Radium.Graphics.Texture;
import com.google.gson.*;

import java.lang.reflect.Type;

public class TextureTypeAdapter implements JsonSerializer<Texture>, JsonDeserializer<Texture> {

    @Override
    public Texture deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String path = json.getAsString();
        return new Texture(path);
    }

    @Override
    public JsonElement serialize(Texture src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.filepath);
    }
}
