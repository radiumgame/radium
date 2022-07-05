package Radium.Editor.Profiling;

import Radium.Engine.Application;
import Radium.Engine.Time;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiCond;

import java.util.ArrayList;
import java.util.List;

public class ProfilingStats {

    private static List<Float> frameRates = new ArrayList<>();
    private static int MaxFrameRateLength = 240;
    private static float UpdateRate = 0.25f;

    private static float time;

    protected ProfilingStats() {}

    public static void Initialize() {
        for (int i = 0; i < MaxFrameRateLength; i++) {
            frameRates.add(0f);
        }
    }

    public static void Update() {
        time += Time.deltaTime;
        if (time > UpdateRate) {
            AddFrameRate(Application.FPS);
            time = 0;
        }
    }

    public static void SetMaxFrameRateLength(int length) {
        MaxFrameRateLength = length;
    }

    public static void AddFrameRate(float frameRate) {
        frameRates.add(frameRate);
        if (frameRates.size() > MaxFrameRateLength) {
            frameRates.remove(0);
        }
    }

    public static List<Float> GetFrameRates() {
        return frameRates;
    }

    public static float GetFrameRate(int index) {
        return frameRates.get(index);
    }

    public static float GetMaxFrameRate() {
        float max = 0;
        for (int i = 0; i < frameRates.size(); i++) {
            if (frameRates.get(i) > max) {
                max = frameRates.get(i);
            }
        }

        return max;
    }

    public static int GetFrameRateAverage() {
        int sum = 0;
        for (int i = 0; i < frameRates.size(); i++) {
            sum += frameRates.get(i);
        }
        return sum / frameRates.size();
    }

    public static void DrawFPSGraph() {
        ImPlot.setNextPlotLimitsX(0, 60, ImGuiCond.Always);
        ImPlot.setNextPlotLimitsY(0, GetMaxFrameRate() + 10, ImGuiCond.Always);
        if (ImPlot.beginPlot("Frame Rate", "Time (Seconds)", "FPS")) {
            Float[] xAxis = new Float[MaxFrameRateLength];
            Float[] yAxis = new Float[MaxFrameRateLength];

            for (int i = 0; i < MaxFrameRateLength; i++) {
                xAxis[i] = i / 4.0f;
                yAxis[i] = GetFrameRate(i);
            }
            ImPlot.plotLine("Frame Rate", xAxis, yAxis);

            ImPlot.endPlot();
        }
    }

}
