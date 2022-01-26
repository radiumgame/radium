package RadiumEditor;

import Radium.EventSystem.EventSystem;
import Radium.EventSystem.Events.Event;
import Radium.EventSystem.Events.EventType;
import Radium.Graphics.Texture;
import Radium.Input.Keys;
import Radium.SceneManagement.Scene;
import Radium.SceneManagement.SceneManager;
import Radium.System.FileExplorer;
import Radium.Window;
import imgui.ImGui;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class MenuBar {

    private static int Play, Stop;

    protected MenuBar() {}

    public static void Initialize() {
        Play = new Texture("EngineAssets/Editor/menubarplay.png").textureID;
        Stop = new Texture("EngineAssets/Editor/menubarstop.png").textureID;

        KeyBindManager.RegisterKeybind(new Keys[] { Keys.F5 }, () -> {
            EventSystem.Trigger(null, new Event(EventType.Play));
        });
        KeyBindManager.RegisterKeybind(new Keys[] { Keys.F6 }, () -> {
            EventSystem.Trigger(null, new Event(EventType.Stop));
        });
    }

    public static void RenderMenuBar() {
        if (ImGui.beginMainMenuBar()) {

            if (ImGui.beginMenu("File")) {

                if (ImGui.menuItem("New Scene")) {
                    String newScenePath = FileExplorer.Create("radiumscene");
                    if (newScenePath.isBlank() || newScenePath.isEmpty()) {
                        return;
                    }

                    File file = new File(newScenePath);
                    try {
                        if (!file.createNewFile()) {
                            return;
                        }

                        FileWriter writer = new FileWriter(file);
                        writer.write("[]");
                        writer.flush();
                        writer.close();

                        SceneManager.SwitchScene(new Scene(file.getPath()));
                    } catch (Exception e) {
                        Console.Error(e);
                    }
                }

                if (ImGui.menuItem("Open Scene")) {
                    String openScene = FileExplorer.Choose("radiumscene");

                    if (openScene != null) {
                        SceneManager.SwitchScene(new Scene(openScene));
                    }
                }

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
