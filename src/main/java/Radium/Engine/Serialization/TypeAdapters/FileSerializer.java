package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.Util.FileUtility;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.File;
import java.io.IOException;

public class FileSerializer extends StdSerializer<File> {

    public FileSerializer() {
        super(File.class);
    }

    @Override
    public void serialize(File value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String local = FileUtility.GetLocalPath(value.getAbsolutePath());
        gen.writeString("radiumfile+" + local);
    }

}
