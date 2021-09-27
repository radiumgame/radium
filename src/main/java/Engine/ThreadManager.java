package Engine;

public final class ThreadManager {

    public ThreadManager() {
        throw new UnsupportedOperationException("Cannot instantiate ThreadManager class");
    }

    public static void RunOnNewThread(Runnable action) {
        new Thread(action).start();
    }

}
