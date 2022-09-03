package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.Util.FileUtility;
import Radium.Integration.Project.Project;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class StringSerializer extends StdSerializer<String> {

    public StringSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        boolean file = value.contains(Project.Current().root);

        if (!file) {
            gen.writeString(value);
            return;
        }

        String local = FileUtility.GetLocalPath(value);
        gen.writeString("radiumfile+" + local);
    }

}
