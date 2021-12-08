package RadiumEditor;

import imgui.ImGui;

public abstract class EditorWindow {

    public boolean Render = false;
    public String MenuName = "Editor Window";

    public abstract void Start();
    public abstract void RenderGUI();

    public boolean StartWindow() {
        if (Render == false) return false;

        ImGui.begin(MenuName);
        return true;
    }

    public void EndWindow() {
        if (ImGui.button("Close")) {
            Render = false;
        }

        ImGui.end();
    }

}
