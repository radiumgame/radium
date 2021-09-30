package Editor;

import Engine.Window;
import imgui.ImGui;

public class MenuBar {

    public static void RenderMenuBar() {
        if (ImGui.beginMainMenuBar()) {

            if (ImGui.beginMenu("File")) {

                ImGui.separator();

                if (ImGui.menuItem("Exit")) {
                    Window.Close();
                }

                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Edit")) {
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Editor Windows")) {
                for (EditorWindow window : Editor.GetAllEditorWindows()) {
                    if (ImGui.menuItem(window.MenuName)) {
                        window.Render = true;
                    }
                }

                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }
    }

}
