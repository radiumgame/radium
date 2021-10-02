package Engine;

import Engine.Util.NonInstantiatable;

public final class ThreadManager extends NonInstantiatable {

    public static void RunOnNewThread(Runnable action) {
        Thread newThread = new Thread(action);
        newThread.start();
    }

}
