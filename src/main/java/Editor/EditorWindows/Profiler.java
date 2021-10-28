package Editor.EditorWindows;

import Editor.EditorWindow;
import Engine.Application;
import Engine.Math.Mathf;
import imgui.ImGui;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import imgui.flag.ImGuiWindowFlags;

public class Profiler extends EditorWindow {

    private OperatingSystemMXBean os;

    public Profiler() {
        MenuName = "Profiler";
    }

    @Override
    public void Start() {
        os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    @Override
    public void RenderGUI() {
        if (!StartWindow()) return;

        ImGui.text("FPS: " + (int) Application.FPS);

        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();

        ImGui.text("CPU Usage: " + Mathf.Round((float)os.getCpuLoad() * 100) + "%");

        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();

        ImGui.text("OS: " + os.getName());
        ImGui.text("Architecture: " + os.getArch());
        ImGui.text("Cores: " + os.getAvailableProcessors());
        
        EndWindow();
    }
}
