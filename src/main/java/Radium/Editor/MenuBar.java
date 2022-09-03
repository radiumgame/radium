package Radium.Editor;

import Radium.Engine.Graphics.RendererType;
import Radium.Engine.Graphics.Renderers.Renderers;
import Radium.Integration.Project.Project;
import Radium.Engine.EventSystem.EventSystem;
import Radium.Engine.EventSystem.Events.Event;
import Radium.Engine.EventSystem.Events.EventType;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Input.Keys;
import Radium.Engine.SceneManagement.Scene;
import Radium.Engine.SceneManagement.SceneManager;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.Util.ThreadUtility;
import Radium.Engine.Window;
import Radium.Runtime;
import imgui.*;
import imgui.flag.ImDrawFlags;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import org.lwjgl.glfw.GLFW;

import java.io.*;

/**
 * Window menu bar
 */
public class MenuBar {

    private static int Play, Stop;
    private static int Logo;
    private static int Minimize, Maximize, Unmaximize, Close;

    protected MenuBar() {}

    /**
     * Initialize textures and keybinds
     */
    public static void Initialize() {
        Play = new Texture("EngineAssets/Editor/menubarplay.png").GetTextureID();
        Stop = new Texture("EngineAssets/Editor/menubarstop.png").GetTextureID();
        Logo = new Texture("EngineAssets/Textures/Icon/icon.png").GetTextureID();

        Minimize = new Texture("EngineAssets/Editor/Window/minimize.png").GetTextureID();
        Maximize = new Texture("EngineAssets/Editor/Window/maximize.png").GetTextureID();
        Unmaximize = new Texture("EngineAssets/Editor/Window/unmaximize.png").GetTextureID();
        Close = new Texture("EngineAssets/Editor/Window/close.png").GetTextureID();

        KeyBindManager.RegisterKeybind(new Keys[] { Keys.LeftCtrl, Keys.O }, MenuBar::OpenScene);
        KeyBindManager.RegisterKeybind(new Keys[] { Keys.LeftCtrl, Keys.N }, MenuBar::NewScene);
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
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0f, 12f);
        if (ImGui.beginMainMenuBar()) {
            ImGui.image(Logo, 42.5f, 42.5f);

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 10f, 42.5f);
            if (ImGui.beginMenu("File")) {
                ImGui.popStyleVar();
                if (ImGui.menuItem("New Scene", "CTRL+N")) {
                    NewScene();
                }

                if (ImGui.menuItem("Open Scene", "CTRL+O")) {
                    OpenScene();
                }

                if (ImGui.menuItem("Save Scene", "CTRL+S")) {
                    SceneManager.GetCurrentScene().Save();
                }

                if (ImGui.menuItem("Project Settings")) {
                    ProjectSettings.Render = true;
                }

                if (ImGui.menuItem("Open VSCode")) {
                    ThreadUtility.Run(() -> {
                        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "code", Project.Current().assets);
                        builder.redirectErrorStream(true);
                        try {
                            Process p = builder.start();
                            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                            String line;
                            do {
                                line = r.readLine();
                            } while (line != null);
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
            } else {
                ImGui.popStyleVar();
            }

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 10f, 42.5f);
            if (ImGui.beginMenu("Edit")) {
                ImGui.popStyleVar();
                if (ImGui.menuItem("Preferences")) {
                    Preferences.Show();
                }

                ImGui.endMenu();
            } else {
                ImGui.popStyleVar();
            }

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 10f, 42.5f);
            if (ImGui.beginMenu("Run")) {
                ImGui.popStyleVar();
                RenderPlayStop();

                ImGui.endMenu();
            } else {
                ImGui.popStyleVar();
            }

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 10f, 42.5f);
            if (ImGui.beginMenu("Editor Windows")) {
                ImGui.popStyleVar();
                for (EditorWindow window : Editor.GetAllEditorWindows()) {
                    if (ImGui.menuItem(window.MenuName)) {
                        window.Start();
                        window.Render = true;
                    }
                }

                if (ImGui.menuItem("Node Scripting")) {
                    NodeScripting.Render = true;
                }

                ImGui.endMenu();
            } else {
                ImGui.popStyleVar();
            }

            RenderProjectName();
            RenderWindowControls();

            ImGui.endMainMenuBar();
        }
        ImGui.popStyleVar();
    }

    private static void NewScene() {
        String newScenePath = FileExplorer.Create("radium");
        if (!FileExplorer.IsPathValid(newScenePath)) {
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

        if (FileExplorer.IsPathValid(openScene)) {
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

    private static void RenderProjectName() {
        String name = Project.Current().name;
        ImVec2 size = new ImVec2();
        ImGui.calcTextSize(size, name);

        ImGui.setCursorPosX(ImGui.getWindowWidth() - 500.0f);
        ImVec2 cursorPos = ImGui.getCursorScreenPos();
        ImDrawList list = ImGui.getWindowDrawList();
        float rightPos = ImGui.getWindowWidth() - 250.0f;

        list.addRect(rightPos - size.x - 40.0f, cursorPos.y - 2.5f, rightPos, cursorPos.y + 30f, ImGui.getColorU32(ImGuiCol.TabHovered), 5f, ImDrawFlags.RoundCornersBottom, 3f);
        list.addRectFilled(rightPos - size.x - 40.0f, cursorPos.y, rightPos, cursorPos.y + 30f, ImGui.getColorU32(ImGuiCol.Button), 5f, ImDrawFlags.RoundCornersBottom);
        list.addText(rightPos - size.x - 20.0f, cursorPos.y + 4f, ImGui.getColorU32(ImGuiCol.Text), name);
    }

    private static void RenderWindowControls() {
        ImVec4 menuBar = ImGui.getStyle().getColor(ImGuiCol.MenuBarBg);
        ImGui.setCursorPosX(ImGui.getWindowWidth() - 135.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 0f);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, 0f, 0f);
        ImGui.pushStyleColor(ImGuiCol.Button, menuBar.x, menuBar.y, menuBar.z, menuBar.w);

        ImGui.setCursorScreenPos(ImGui.getCursorScreenPosX(), -5);
        if (ImGui.imageButton(Minimize, 45f, 35f)) {
            Window.Minimize();
        }

        int maxIcon = GLFW.glfwGetWindowAttrib(Window.GetRaw(), GLFW.GLFW_MAXIMIZED) == GLFW.GLFW_TRUE ? Unmaximize : Maximize;
        if (ImGui.imageButton(maxIcon, 45f, 35f)) {
            Window.Maximize();
        }

        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1.0f, 0.0f, 0.0f, 1.0f);
        if (ImGui.imageButton(Close, 45f, 35f)) {
            Window.Close();
        }

        ImGui.popStyleColor(2);
        ImGui.popStyleVar(2);
    }

}
