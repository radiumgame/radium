package RadiumRuntime;

import Radium.UI.Text.CFont;
import Radium.UI.UIRenderer;
import RadiumEditor.Debug.Debug;
import RadiumEditor.Editor;
import RadiumEditor.Gui;
import Radium.*;
import Radium.Audio.Audio;
import RadiumEditor.Debug.GridLines;
import Radium.EventSystem.EventSystem;
import Radium.EventSystem.Events.Event;
import Radium.EventSystem.Events.EventType;
import RadiumEditor.Debug.Gizmo.Gizmo;
import RadiumEditor.Debug.Gizmo.GizmoManager;
import Radium.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Graphics.Lighting.Lighting;
import Radium.Graphics.Renderers.EditorRenderer;
import Radium.Graphics.Renderers.Renderers;
import Radium.Graphics.Shadows.Shadows;
import Radium.Math.Vector.Vector3;
import Radium.Objects.EditorCamera;
import Radium.Physics.PhysicsManager;
import Radium.SceneManagement.Scene;
import Radium.SceneManagement.SceneManager;
import RadiumEditor.*;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Runtime {

    public static void main(String[] args) {
        Start();
    }

    private static float fps = 1;
    private static long fpsTime;

    public static String title = "Radium3D";
    private static boolean Minimized;

    private static boolean LogVersions = false;

    protected Runtime() {}

    private static void Start() {
        Window.CreateWindow(1920, 1080, "Radium3D");
        Window.SetIcon("EngineAssets/Textures/Icon/icondark.png");
        Window.Maximize();

        Variables.Settings = Settings.TryLoadSettings("EngineAssets/editor.settings");

        Renderers.Initialize();
        UIRenderer.Initialize();
        Lighting.Initialize();
        Shadows.CreateFramebuffer();

        Variables.EditorCamera = new EditorCamera();
        Variables.EditorCamera.transform.position = new Vector3(-4f, 1.5f, 4f);
        Variables.EditorCamera.transform.rotation = new Vector3(15, 45, 0);

        Initialize();

        CFont font = new CFont("EngineAssets/Fonts/PTSans/PTSans-Regular.ttf", 64);

        if (LogVersions) {
            Console.Log("OpenGL Version: " + GLFW.glfwGetVersionString().split(" Win32")[0]);
            Console.Log("GLSL Version: 3.30");
            Console.Log("ImGui Version: " + ImGui.getVersion());
            Console.Log("PhysX Version: 4.14");
        }

        Application application = new Application();
        application.Initialize();
        Application.IsEditor = true;

        EditorSave.LoadEditorState();
        EventSystem.Trigger(null, new Event(EventType.Load));

        Variables.Settings.Enable();

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
        //EditorSave.SaveEditorState();
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

        ShadowRender();

        Window.GetFrameBuffer().Bind();
        PreRender();

        Lighting.UpdateUniforms();
        SceneManager.GetCurrentScene().Update();
        Skybox.Render();

        if (!Application.Playing) {
            GridLines.Render();
            Debug.Render();

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

        Viewport.Render();
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

    private static void ShadowRender() {
        DepthFramebuffer.DepthTesting = true;
        GL11.glViewport(0, 0, Shadows.ShadowFramebufferSize, Shadows.ShadowFramebufferSize);
        Shadows.framebuffer.Bind();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        SceneManager.GetCurrentScene().Render();
        Shadows.framebuffer.Unbind();
        DepthFramebuffer.DepthTesting = false;
        GL11.glViewport(0, 0, 1920, 1080);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    private static void Initialize() {
        Editor.Initialize();
        MenuBar.Initialize();
        Viewport.Initialize();
        ProjectExplorer.Initialize();
        Inspector.Initialize();

        EditorRenderer.Initialize();
        GridLines.Initialize();

        Skybox.Initialize();

        KeyBindManager.Initialize();
        PhysicsManager.Initialize();
    }

}
