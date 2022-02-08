package RadiumEditor;

import imgui.ImGui;

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

    /**
     * When window is opened
     */
    public abstract void Start();

    /**
     * GUI to draw
     */
    public abstract void RenderGUI();

    /**
     * Starts a new window
     * @return Window is open or collapsed
     */
    public boolean StartWindow() {
        if (Render == false) return false;

        ImGui.begin(MenuName);
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
