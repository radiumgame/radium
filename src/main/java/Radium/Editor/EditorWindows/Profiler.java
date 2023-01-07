package Radium.Editor.EditorWindows;

import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Graphics.Mesh;
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
        ImGui.text("Thread Count: " + os.getAvailableProcessors());

        ProfilingStats.DrawFPSGraph();

        ImGui.text("Total Vertices: " + MeshFilter.VertexCount);
        ImGui.text("Total Triangles: " + MeshFilter.TriangleCount);
        MeshFilter.VertexCount = 0;
        MeshFilter.TriangleCount = 0;
    }

}