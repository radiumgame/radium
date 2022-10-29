package Radium.Editor;

import Radium.Engine.System.FileExplorer;
import Radium.Integration.Project.Project;
import imgui.ImGui;

public class ProjectSettings {

    public static boolean Render = false;

    protected ProjectSettings() {}

    public static void Render() {
        if (!Render) return;

        ImGui.begin("Project Settings");

        Project project = Project.Current();
        project.configuration.projectName = EditorGUI.InputString("Project Name", project.configuration.projectName);
        project.configuration.projectIcon = EditorGUI.InputString("Project Icon", project.configuration.projectIcon);
        ImGui.sameLine();
        if (ImGui.button("Choose")) {
            String res = FileExplorer.Choose("png,jpg,jpeg,bmp;");
            if (FileExplorer.IsPathValid(res)) {
                project.configuration.projectIcon = res;
            }
        }
        project.configuration.projectBootup = EditorGUI.InputString("Project Bootup", project.configuration.projectBootup);
        ImGui.sameLine();
        if (ImGui.button("Choose")) {
            String res = FileExplorer.Choose("png,jpg,jpeg,bmp;");
            if (FileExplorer.IsPathValid(res)) {
                project.configuration.projectBootup = res;
            }
        }

        if (ImGui.button("Save Settings")) {
            project.SaveConfiguration();
        }
        ImGui.sameLine();
        if (ImGui.button("Close")) {
            Render = false;
        }

        ImGui.end();
    }

}
