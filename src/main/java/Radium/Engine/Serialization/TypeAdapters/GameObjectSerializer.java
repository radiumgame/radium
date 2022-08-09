package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.Component;
import Radium.Engine.Objects.GameObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.List;

public class GameObjectSerializer extends StdSerializer<GameObject> {

    public GameObjectSerializer() {
        super(GameObject.class);
    }

    @Override
    public void serialize(GameObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        try {
            gen.writeStartObject();
            gen.writeStringField("name", value.name);
            gen.writeObjectField("transform", value.transform);
            gen.writeStringField("id", value.id);
            gen.writeStringField("parentID", value.GetParent() != null ? value.GetParent().id : "");

            List<Component> componentList = value.GetComponents();
            Component[] components = new Component[componentList.size()];
            componentList.toArray(components);
            gen.writeArrayFieldStart("components");
            for (Component component : components) {
                gen.writeObject(component);
            }
            gen.writeEndArray();
            gen.writeEndObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
