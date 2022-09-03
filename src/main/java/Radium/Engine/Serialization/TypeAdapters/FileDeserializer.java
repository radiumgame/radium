package Radium.Engine.Serialization.TypeAdapters;

import Radium.Editor.Console;
import Radium.Engine.Util.FileUtility;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.File;
import java.io.IOException;

public class FileDeserializer extends StdDeserializer<File> {

    public FileDeserializer() {
        super(File.class);
    }

    @Override
    public File deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String localPath = p.getValueAsString();
        localPath = localPath.replace("radiumfile+", "");
        localPath = FileUtility.RevertLocalPath(localPath);
        return new File(localPath);
    }

}
