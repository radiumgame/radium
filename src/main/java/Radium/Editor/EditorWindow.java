package Radium.Editor;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

/**
 * Framework for building editor windows
 */
public abstract class EditorWindow {

    /**
     * Determines whether to render the window
     */
    public boolean Render = false;

    /**
     * Menu Item name
     */
    public String MenuName = "Editor Window";

    private String windowName;

    /**
     * When window is opened
     */
    public abstract void Start();

    /**
     * GUI to draw
     */
    public abstract void RenderGUI();

    public void SetWindowName(String name) {
        this.windowName = name;
    }

    /**
     * Starts a new window
     * @return Window is open or collapsed
     */
    public boolean StartWindow() {
        if (!Render) return false;

        ImGui.begin(windowName == null ? MenuName : windowName, ImGuiWindowFlags.MenuBar);
        return true;
    }

    /**
     * Ends window
     * ** REQUIRES {@link #StartWindow() StartWindow()} to be called **
     */
    public void EndWindow() {
        if (ImGui.button("Close")) {
            Render = false;
        }

        ImGui.end();
    }

}
