package Editor;

import Engine.Util.NonInstantiatable;
import Engine.Window;
import imgui.ImGui;

public final class MenuBar extends NonInstantiatable {

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

                if (ImGui.menuItem("Preferences")) {
                    Preferences.Show();
                }

                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Editor Windows")) {
                for (EditorWindow window : Editor.GetAllEditorWindows()) {
                    if (ImGui.menuItem(window.MenuName)) {
                        window.Start();
                        window.Render = true;
                    }
                }

                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }
    }

}
