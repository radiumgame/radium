package Radium.Serialization.TypeAdapters;

import Radium.Scripting.Nodes.NodeInput;
import RadiumEditor.Console;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NodeInputTypeAdapter implements JsonDeserializer<NodeInput> {

    @Override
    public NodeInput deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        int ID = jsonObject.get("ID").getAsInt();
        JsonObject type = jsonObject.getAsJsonObject("type");
        String className = type.get("className").getAsString();

        Object obj = null;
        try {
            JsonElement jsonObj = jsonObject.get("object");
            obj = context.deserialize(jsonObj, Class.forName(className));
        } catch (Exception e) {
            Console.Error(e);
        }

        try {
            NodeInput input = new NodeInput(null);
            input.name = name;
            input.ID = ID;
            input.links = new ArrayList<>();
            input.type = Class.forName(className);
            input.object = obj;

            return input;
        } catch (Exception e) {
            Console.Error(e);
        }

        return null;
    }

}
