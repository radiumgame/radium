package Engine;

import Engine.Util.NonInstantiatable;

public final class Time extends NonInstantiatable {

    private static float timeStarted = System.nanoTime();
    public static float deltaTime;

    public static float GetTime() {
        return (float)((System.nanoTime() - timeStarted) * 1E-9);
    }

}
