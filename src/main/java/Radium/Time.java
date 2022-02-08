package Radium;

/**
 * Stores time settings and variables
 */
public class Time {

    private static float timeStarted = System.nanoTime();

    /**
     * The time between frames:
     * E.G. 1/60 = 0.016
     */
    public static float deltaTime;

    protected Time() {}

    /**
     * Returns the time since the editor has started playing in seconds
     */
    public static float GetTime() {
        return (float)((System.nanoTime() - timeStarted) * 1E-9);
    }

}
