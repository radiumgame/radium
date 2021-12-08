package Radium.Serialization.TypeAdapters;

import Radium.Component;
import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Math.Transform;
import Radium.Objects.GameObject;
import com.google.gson.*;

import java.lang.reflect.Type;

public class GameObjectTypeAdapter implements JsonDeserializer<GameObject> {

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String name = object.get("name").getAsString();
        JsonArray components = object.get("components").getAsJsonArray();
        JsonElement transformElement = object.get("transform");
        Transform transform = context.deserialize(transformElement, Transform.class);

        GameObject newObject = new GameObject();
        newObject.transform = transform;
        newObject.name = name;

        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);

            if (c.getClass() == MeshRenderer.class) {
                newObject.AddComponent(c);
            } else if (c.getClass() == MeshFilter.class) {
                MeshFilter filter = (MeshFilter)c;
                newObject.AddComponent(new MeshFilter(filter.mesh));
            } else {
                newObject.AddComponent(c);
            }
        }

        return newObject;
    }

}
