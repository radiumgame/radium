package Radium;

public class ThreadManager {

    protected ThreadManager() {}

    public static void RunOnNewThread(Runnable action) {
        Thread newThread = new Thread(action);
        newThread.start();
    }

}
