package Radium.Integration.Project;

import Radium.Engine.Application;
import Radium.Editor.Console;

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
            for (WatchEvent<?> event : events) {
                for (AssetsListener listener : listeners) {
                    FileEvent fileEvent = new FileEvent();
                    fileEvent.event = event;
                    fileEvent.listener = listener;
                    fileEvent.currentDirectory = currentDirectory;
                    Assets.eventsToCall.add(fileEvent);
                }
            }

            key.reset();
        }
    }

    public void RegisterListener(AssetsListener listener) {
        listeners.add(listener);
    }

    public void Destroy() {
        if (thread != null) thread.stop();
        listeners.clear();
    }

}
