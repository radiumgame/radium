package Editor;

import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Graphics.Texture;
import Engine.SceneManagement.SceneManager;
import Engine.Util.NonInstantiatable;
import Engine.Window;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

public final class MenuBar extends NonInstantiatable {

    private static int Play, Stop;

    public static void Initialize() {
        Play = new Texture("EngineAssets/Editor/menubarplay.png").textureID;
        Stop = new Texture("EngineAssets/Editor/menubarstop.png").textureID;

        KeyBindManager.RegisterKeybind(new int[] { GLFW.GLFW_KEY_F5 }, () -> {
            EventSystem.Trigger(null, new Event(EventType.Play));
        });
        KeyBindManager.RegisterKeybind(new int[] { GLFW.GLFW_KEY_F6 }, () -> {
            EventSystem.Trigger(null, new Event(EventType.Stop));
        });
    }

    public static void RenderMenuBar() {
        if (ImGui.beginMainMenuBar()) {

            if (ImGui.beginMenu("File")) {

                if (ImGui.menuItem("Save Scene", "CTRL+S")) {
                    SceneManager.GetCurrentScene().Save();
                }

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

            if (ImGui.beginMenu("Run")) {
                ImGui.image(Play, 17, 17);
                ImGui.sameLine();
                if (ImGui.menuItem("Play", "F5")) {
                    EventSystem.Trigger(null, new Event(EventType.Play));
                }

                ImGui.image(Stop, 17, 17);
                ImGui.sameLine();
                if (ImGui.menuItem("Stop", "F6")) {
                    EventSystem.Trigger(null, new Event(EventType.Stop));
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
