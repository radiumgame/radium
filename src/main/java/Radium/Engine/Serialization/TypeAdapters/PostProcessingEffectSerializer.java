package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.PostProcessing.PostProcessingEffect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PostProcessingEffectSerializer extends StdSerializer<PostProcessingEffect> {

    public PostProcessingEffectSerializer() {
        super(PostProcessingEffect.class);
    }

    @Override
    public void serialize(PostProcessingEffect value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
        String json = mapper.writeValueAsString(value);

        gen.writeStartObject();
        gen.writeStringField("type", value.getClass().getCanonicalName());
        gen.writeFieldName("properties");
        gen.writeRaw(": " + json);
        gen.writeEndObject();
    }
}
