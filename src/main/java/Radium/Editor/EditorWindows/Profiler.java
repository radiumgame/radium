package Radium.Editor.EditorWindows;

import Radium.Engine.Objects.GameObject;
import Radium.Editor.EditorWindow;
import Radium.Engine.Application;
import Radium.Engine.Math.Mathf;
import Radium.Editor.Profiling.ProfilingStats;
import Radium.Editor.Profiling.Timers;
import imgui.ImGui;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

/**
 * A simple profiler that tracks FPS and other stats
 */
public class Profiler extends EditorWindow {

    private OperatingSystemMXBean os;

    /**
     * Creates empty instance
     */
    public Profiler() {
        MenuName = "Profiler";
    }


    public void Start() {
        os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }


    public void RenderGUI() {
        ImGui.text("FPS: " + (int) Application.FPS);

        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();

        ImGui.text("CPU Usage: " + Mathf.Round((float) os.getCpuLoad() * 100) + "%");

        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();

        ImGui.text("OS: " + os.getName());
        ImGui.text("Architecture: " + os.getArch());
        ImGui.text("Cores: " + os.getAvailableProcessors());

        ProfilingStats.DrawFPSGraph();

        ImGui.text("Rendering: " + FormatMS(Timers.GetRenderTime()));
        if (ImGui.collapsingHeader("Individual Meshes")) {
            ImGui.indent();
            for (GameObject mesh : Timers.GetMeshes().keySet()) {
                ImGui.text(mesh.name + ": " + FormatMS(Timers.GetRenderTimeOfMesh(mesh)));
            }
            ImGui.unindent();
        }
    }

    private static String FormatMS(long ms) {
        String result;
        if (ms < 1) {
            result = "<1ms";
        } else {
            result = ms + "ms";
        }

        return result;
    }

}