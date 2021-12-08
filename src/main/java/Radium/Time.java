package Radium;

public class Time {

    private static float timeStarted = System.nanoTime();
    public static float deltaTime;

    protected Time() {}

    public static float GetTime() {
        return (float)((System.nanoTime() - timeStarted) * 1E-9);
    }

}
