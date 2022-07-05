package Radium.Editor.Profiling;

import Radium.Engine.Objects.GameObject;

import java.util.HashMap;

public class Timers {

    private static ProfilingTimer cumulativeRendering = new ProfilingTimer(false);
    private static HashMap<GameObject, ProfilingTimer> renderingTimers = new HashMap<>();

    public static void StartRenderingTimer() {
        renderingTimers.clear();
        cumulativeRendering.StartSampling();
    }

    public static void EndRenderingTimer() {
        cumulativeRendering.EndSampling();
    }

    public static ProfilingTimer StartMeshRenderingTimer(GameObject mesh) {
        ProfilingTimer timer = new ProfilingTimer(false);
        renderingTimers.put(mesh, timer);
        timer.StartSampling();

        return timer;
    }

    public static void EndMeshRenderingTimer(ProfilingTimer timer) {
        timer.EndSampling();
    }

    public static long GetRenderTimeOfMesh(GameObject mesh) {
        return renderingTimers.getOrDefault(mesh, new ProfilingTimer(false)).GetTime();
    }

    public static long GetRenderTime() {
        return cumulativeRendering.GetTime();
    }

    public static HashMap<GameObject, ProfilingTimer> GetMeshes() {
        return renderingTimers;
    }

}
