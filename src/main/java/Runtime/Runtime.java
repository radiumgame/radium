package Runtime;

import Editor.Editor;
import Editor.Gui;
import Engine.*;
import Engine.Audio.Audio;
import Engine.Debug.GridLines;
import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Debug.Gizmo.Gizmo;
import Engine.Debug.Gizmo.GizmoManager;
import Engine.Graphics.Lighting;
import Engine.Graphics.Renderers.EditorRenderer;
import Engine.Graphics.Renderers.Renderers;
import Engine.Graphics.Texture;
import Engine.Math.Vector.Vector3;
import Engine.Objects.EditorCamera;
import Engine.Physics.PhysicsManager;
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

    private static boolean LogVersions = false;

    private static void Start() {
        Window.CreateWindow(1920, 1080, "Radium3D");
        Window.SetIcon("EngineAssets/Textures/Icon/icondark.png");
        Window.Maximize();

        Renderers.Initialize();
        Lighting.Initialize();

        Variables.EditorCamera = new EditorCamera();
        Variables.EditorCamera.transform.position = new Vector3(-4f, 1.5f, 4f);
        Variables.EditorCamera.transform.rotation = new Vector3(15, 45, 0);

        Initialize();

        if (LogVersions) {
            Console.Log("OpenGL Version: " + GLFW.glfwGetVersionString().split(" Win32")[0]);
            Console.Log("GLSL Version: 3.30");
            Console.Log("ImGui Version: " + ImGui.getVersion());
            Console.Log("PhysX Version: 4.14");
        }

        Application application = new Application();
        application.Initialize();
        Application.IsEditor = true;

        SceneManager.SwitchScene(new Scene("Assets/Scenes/default.radiumscene"));

        EventSystem.Trigger(null, new Event(EventType.Load));
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
        EventSystem.Trigger(null, new Event(EventType.Exit));

        Window.Destroy();
    }

    private static void Update() {
        Minimized = GLFW.glfwGetWindowAttrib(Window.GetRaw(), GLFW.GLFW_ICONIFIED) == 1 ? true : false;

        Window.Update();
        Audio.Update();

        KeyBindManager.Update();
        if (Application.Playing) PhysicsManager.Update();

        Variables.EditorCamera.Update();
        Window.GetFrameBuffer().Bind();

        PreRender();

        Lighting.UpdateUniforms();
        SceneManager.GetCurrentScene().Update();
        Skybox.Render();

        if (!Application.Playing) {
            GridLines.Render();

            for (Gizmo gizmo : GizmoManager.gizmos) {
                gizmo.Update();
            }
        }

        Window.GetFrameBuffer().Unbind();

        RenderGUI();
        Editor.RenderEditorWindows();

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

        if (!Minimized) {
            Gui.StartFrame();
            ImGui.newFrame();
        }
    }

    private static void PostRender() {
        if (!Minimized) {
            ImGui.render();
            Gui.EndFrame();
        }

        GLFW.glfwPollEvents();
        Window.SwapBuffers();
    }

    private static void Initialize() {
        Editor.Initialize();
        MenuBar.Initialize();
        ProjectExplorer.Initialize();
        Inspector.Initialize();
        EditorRenderer.Initialize();
        GridLines.Initialize();
        Skybox.Initialize();
        Skybox.SetSkyboxTexture(new Texture("EngineAssets/Textures/Skybox/Skybox.jpg"));
        KeyBindManager.Initialize();
        PhysicsManager.Initialize();
    }

}
