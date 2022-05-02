package Integration.Project;

import Radium.Application;
import Radium.Util.ThreadUtility;
import RadiumEditor.Console;

import java.io.File;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectFiles {

    private WatchService watchService;
    private String currentDirectory;

    private Thread thread;

    private List<AssetsListener> listeners = new ArrayList<>();

    public void Initialize(String directory) {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(directory);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            thread = new Thread(() -> {
                while (Application.Editor) {
                    try {
                        Update();
                    } catch (Exception e) {}
                }
            });
            thread.start();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Initialize() {
        Initialize(Project.Current().assets);
    }

    private void Update() throws Exception {
        WatchKey key;
        while ((key = watchService.take()) != null) {
            List<WatchEvent<?>> events = key.pollEvents();
            if (events.size() > 1) {
                WatchEvent<?> event = events.get(events.size() - 1);
                events.clear();
                events.add(event);
            }

            for (WatchEvent<?> event : events) {
                File file = new File(currentDirectory + "/" + event.context().toString());
                for (AssetsListener listener : listeners) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        listener.OnFileCreated(file);
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        listener.OnFileDeleted(file);
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        listener.OnFileChanged(file);
                    }
                }
            }

            key.reset();
        }
    }

    public void RegisterListener(AssetsListener listener) {
        listeners.add(listener);
    }

    public void Destroy() {
        thread.stop();
        listeners.clear();
    }

}
