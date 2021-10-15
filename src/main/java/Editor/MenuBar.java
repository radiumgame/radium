package Editor;

import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Graphics.Texture;
import Engine.Util.NonInstantiatable;
import Engine.Window;
import imgui.ImGui;

public final class MenuBar extends NonInstantiatable {

    private static int Play, Stop;

    public static void Initialize() {
        Play = new Texture("EngineAssets/Editor/menubarplay.png").textureID;
        Stop = new Texture("EngineAssets/Editor/menubarstop.png").textureID;
    }

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

            if (ImGui.beginMenu("Run")) {
                ImGui.image(Play, 17, 17);
                ImGui.sameLine();
                if (ImGui.menuItem("Play")) {
                    EventSystem.Trigger(null, new Event(EventType.Play));
                }

                ImGui.image(Stop, 17, 17);
                ImGui.sameLine();
                if (ImGui.menuItem("Stop")) {
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
