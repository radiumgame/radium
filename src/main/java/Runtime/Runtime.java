package Runtime;

import Editor.Editor;
import Editor.Gui;
import Engine.*;
import Engine.Audio.Audio;
import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Gizmo.Gizmo;
import Engine.Gizmo.GizmoManager;
import Engine.Graphics.Lighting;
import Engine.Graphics.Renderers.EditorRenderer;
import Engine.Graphics.Renderers.Renderers;
import Engine.Graphics.Texture;
import Engine.Math.Vector.Vector3;
import Engine.Objects.EditorCamera;
import Engine.SceneManagement.Scene;
import Engine.SceneManagement.SceneManager;
import Editor.*;
import Engine.Util.NonInstantiatable;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public final class Runtime extends NonInstantiatable {

    public static void main(String[] args) {
        Start();
    }

    private static float fps = 1;
    private static long fpsTime;

    public static String title = "Radium3D";
    private static boolean Minimized;

    private static boolean IsBuild = false;

    private static void Start() {
        Window.CreateWindow(1920, 1080, "Radium3D");
        Window.SetIcon("EngineAssets/Textures/icondark.png");
        Window.Maximize();

        Renderers.Initialize();
        Lighting.Initialize();

        Variables.EditorCamera = new EditorCamera();
        Variables.EditorCamera.transform.position = new Vector3(-4f, 1.5f, 4f);
        Variables.EditorCamera.transform.rotation = new Vector3(15, 45, 0);

        Editor.Initialize();
        MenuBar.Initialize();
        ProjectExplorer.Initialize();
        Inspector.Initialize();
        EditorRenderer.Initialize();
        Skybox.Initialize();
        Skybox.SetSkyboxTexture(new Texture("EngineAssets/Textures/Skybox.jpg"));
        KeyBindManager.Initialize();

        Application application = new Application();
        application.Initialize();
        Application.IsEditor = !IsBuild;

        SceneManager.SwitchScene(new Scene("Assets/Scenes/default.radiumscene"));

        EventSystem.Trigger(null, new Event(EventType.Load));
        if (IsBuild) EventSystem.Trigger(null, new Event(EventType.Play));

        float beginTime = Time.GetTime();
        float endTime;
        while (!Window.ShouldClose()) {
            Update();

            endTime = Time.GetTime();
            float dt = endTime - beginTime;
            beginTime = endTime;
            Time.deltaTime = dt;

            fps++;
            if (System.currentTimeMillis() > fpsTime + 1000) {
                Application.FPS = fps;
                fpsTime = System.currentTimeMillis();
                fps = 0;
            }
        }

        SceneManager.GetCurrentScene().Save();
        EventSystem.Trigger(null, new Event(EventType.Exit));

        Window.Destroy();
    }

    private static void Update() {
        Minimized = GLFW.glfwGetWindowAttrib(Window.GetRaw(), GLFW.GLFW_MAXIMIZED) == 1 ? false : true;

        Window.Update();
        Audio.Update();

        KeyBindManager.Update();

        if (!IsBuild) {
            Variables.EditorCamera.Update();
            Window.GetFrameBuffer().Bind();
        }

        PreRender();

        Lighting.UpdateUniforms();
        SceneManager.GetCurrentScene().Update();
        Skybox.Render();

        if (!IsBuild) {
            if (!Application.Playing) {
                for (Gizmo gizmo : GizmoManager.gizmos) {
                    gizmo.Update();
                }
            }

            Window.GetFrameBuffer().Unbind();

            RenderGUI();
            Editor.RenderEditorWindows();
        }

        PostRender();
    }

    private static void RenderGUI() {
        if (Minimized) return;

        Editor.SetupDockspace();

        Editor.Viewport();
        MenuBar.RenderMenuBar();
        SceneHierarchy.Render();
        Inspector.Render();
        Console.Render();
        ProjectExplorer.Render();
        Preferences.Render();

        ImGui.end();
    }

    private static void PreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();

        if (!Minimized || !IsBuild) {
            Gui.StartFrame();
            ImGui.newFrame();
        }
    }

    private static void PostRender() {
        if (!Minimized || !IsBuild) {
            ImGui.render();
            Gui.EndFrame();
        }

        GLFW.glfwPollEvents();
        Window.SwapBuffers();
    }

}
