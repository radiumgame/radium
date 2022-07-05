package Radium.Integration.Project;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public class FileEvent {

    public WatchEvent<?> event;
    public AssetsListener listener;
    public String currentDirectory;

    public void Call() {
        File file = new File(currentDirectory + "/" + event.context().toString());
        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
            listener.OnFileCreated(file);
        } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
            listener.OnFileDeleted(file);
        } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
            listener.OnFileChanged(file);
        }
    }

}
