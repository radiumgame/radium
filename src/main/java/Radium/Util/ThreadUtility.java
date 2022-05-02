package Radium.Util;

import RadiumEditor.Console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThreadUtility {

    private static HashMap<String, Thread> threads = new HashMap<>();
    private static UUIDGenerator ids = new UUIDGenerator();

    protected ThreadUtility() {}

    public static boolean ThreadExists(String threadName) {
        return threads.containsKey(threadName);
    }

    public static String Run(Runnable action) {
        String name = ids.NewUUID();

        Thread thread = new Thread(action);
        thread.setName(name);
        thread.start();

        threads.put(name, thread);
        return name;
    }

    public static void Run(Runnable action, String name) {
        Thread thread = new Thread(action);
        thread.setName(name);
        thread.start();

        threads.put(name, thread);
    }

    public static void Kill(String thread) {
        if (!threads.containsKey(thread)) {
            Console.Error("No thread named: " + thread);
            return;
        }

        threads.get(thread).stop();
    }

}
