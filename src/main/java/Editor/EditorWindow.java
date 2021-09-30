package Editor;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public abstract class EditorWindow {

    public boolean Render = false;
    public String MenuName = "Editor Window";

    public abstract void Start();
    public abstract void RenderGUI();

    public void CheckForWindowClose() {
        if (ImGui.button("Close", 40, 20)) {
            Render = false;
        }
    }

}
