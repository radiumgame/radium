package RadiumEditor;

import Integration.Project.Project;
import Radium.EventSystem.EventSystem;
import Radium.EventSystem.Events.Event;
import Radium.EventSystem.Events.EventType;
import Radium.Graphics.Texture;
import Radium.Input.Keys;
import Radium.SceneManagement.Scene;
import Radium.SceneManagement.SceneManager;
import Radium.System.FileExplorer;
import Radium.Util.ThreadUtility;
import Radium.Window;
import imgui.ImGui;

import java.io.*;
import java.security.Key;

/**
 * Window menu bar
 */
public class MenuBar {

    private static int Play, Stop;

    protected MenuBar() {}

    /**
     * Initialize textures and keybinds
     */
    public static void Initialize() {
        Play = new Texture("EngineAssets/Editor/menubarplay.png").textureID;
        Stop = new Texture("EngineAssets/Editor/menubarstop.png").textureID;

        KeyBindManager.RegisterKeybind(new Keys[] { Keys.LeftCtrl, Keys.O }, () -> {
            OpenScene();
        });
        KeyBindManager.RegisterKeybind(new Keys[] { Keys.LeftCtrl, Keys.N }, () -> {
            NewScene();
        });
        KeyBindManager.RegisterKeybind(new Keys[] { Keys.F5 }, () -> {
            EventSystem.Trigger(null, new Event(EventType.Play));
        });
        KeyBindManager.RegisterKeybind(new Keys[] { Keys.F6 }, () -> {
            EventSystem.Trigger(null, new Event(EventType.Stop));
        });
    }

    /**
     * Render the menu bar
     */
    public static void RenderMenuBar() {
        if (ImGui.beginMainMenuBar()) {

            if (ImGui.beginMenu("File")) {

                if (ImGui.menuItem("New Scene", "CTRL+N")) {
                    NewScene();
                }

                if (ImGui.menuItem("Open Scene", "CTRL+O")) {
                    OpenScene();
                }

                if (ImGui.menuItem("Save Scene", "CTRL+S")) {
                    SceneManager.GetCurrentScene().Save();
                }

                if (ImGui.menuItem("Open VSCode")) {
                    ThreadUtility.Run(() -> {
                        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "code", Project.Current().assets);
                        builder.redirectErrorStream(true);
                        try {
                            Process p = builder.start();
                            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                            String line;
                            while (true) {
                                line = r.readLine();
                                if (line == null) {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            Console.Error(e);
                        }
                    });
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
                RenderPlayStop();

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

    private static void NewScene() {
        String newScenePath = FileExplorer.Create("radium");
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

    private static void OpenScene() {
        String openScene = FileExplorer.Choose("radium");

        if (openScene != null) {
            SceneManager.SwitchScene(new Scene(openScene));
        }
    }

    private static void RenderPlayStop() {
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
    }

}
