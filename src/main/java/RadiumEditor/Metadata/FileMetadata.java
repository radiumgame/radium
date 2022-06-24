package RadiumEditor.Metadata;

import Integration.Project.Project;

import java.util.HashMap;
import java.io.File;

public class FileMetadata {

    private final HashMap<String, Object> Properties = new HashMap<>();
    private final File file;

    public FileMetadata(File file) {
        this.file = file;
    }

    public void AddProperty(String name, Object value) {
        Properties.put(name, value);
        Project.Current().UpdateMetadata(file);
    }

    public Object GetProperty(String name) {
        return Properties.get(name);
    }

}
