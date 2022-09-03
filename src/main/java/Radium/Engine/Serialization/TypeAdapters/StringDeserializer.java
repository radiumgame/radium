package Radium.Engine.Serialization.TypeAdapters;

import Radium.Engine.Util.FileUtility;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class StringDeserializer extends StdDeserializer<String> {

    public StringDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String val = p.getValueAsString();
        boolean file = val.startsWith("radiumfile+");
        if (file) {
            val = val.replace("radiumfile+", "");
            val = FileUtility.RevertLocalPath(val);
        }

        return val;
    }

}
