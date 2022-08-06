package Radium.Engine.Serialization.TypeAdapters;

import Radium.Engine.Components.Rendering.PostProcessing;
import Radium.Editor.Console;
import Radium.Engine.Component;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Serializes components into json
 */
public class ComponentSerializer extends StdSerializer<Component> {

    public ComponentSerializer() {
        super(Component.class);
    }

    public void serialize(Component src, JsonGenerator gen, SerializerProvider provider) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            String json = mapper.writeValueAsString(src);

            gen.writeStartObject(src);
            gen.writeStringField("type", src.getClass().getCanonicalName());
            gen.writeFieldName("properties");
            gen.writeRaw(": " + json);
            gen.writeEndObject();

            if (src.getClass() == PostProcessing.class) {
                //jsonGenerator.writeObject(((PostProcessing) src).effects.toArray());
                //jsonGenerator.writeObject(((PostProcessing) src).custom.toArray());
            }
        } catch (Exception e) {
            Console.Error(e);
        }
    }
}
