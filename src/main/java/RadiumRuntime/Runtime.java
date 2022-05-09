package RadiumRuntime;

import Integration.Project.Assets;
import Integration.Project.Project;
import Radium.Graphics.Framebuffer.Framebuffer;
import Radium.Graphics.RenderQueue;
import Radium.PostProcessing.PostProcessing;
import Radium.System.FileExplorer;
import Radium.System.Popup;
import Radium.UI.NanoVG.NVG;
import Radium.UI.Legacy.UIRenderer;
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
import Radium.SceneManagement.SceneManager;
import RadiumEditor.*;
import RadiumEditor.MousePicking.MousePickingCollision;
import RadiumEditor.Profiling.ProfilingStats;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;

/**
 * The main application, starts the program
 */
public class Runtime {

    /**
     * Starts program
     * @param args
     */
    public static void main(String[] args) {
        Start();
    }

    private static float fps = 1;
    private static long fpsTime;

    /**
     * Window title
     */
    public static String title = "Radium3D";
    private static boolean Minimized;

    private static boolean LogVersions = false;

    public static Framebuffer renderFramebuffer;

    protected Runtime() {}

    private static void Start() {
        String directory = FileExplorer.ChooseDirectory();
        if (directory == null) {
            System.exit(0);
        }
        new Project(directory);

        Window.CreateWindow(1920, 1080, "Radium3D", true);
        Window.SetIcon("EngineAssets/Textures/Icon/icon.png");
        Window.Maximize();

        renderFramebuffer = new Framebuffer(1920, 1080);

        Variables.Settings = Settings.TryLoadSettings("EngineAssets/editor.settings");

        Renderers.Initialize();
        UIRenderer.Initialize();
        Lighting.Initialize();
        Shadows.CreateFramebuffer();

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

        Project.Current().ApplyConfiguration();
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
        Project.Current().SaveConfiguration();
        EventSystem.Trigger(null, new Event(EventType.Exit));

        Window.Destroy();
    }

    private static void Update() {
        Minimized = GLFW.glfwGetWindowAttrib(Window.GetRaw(), GLFW.GLFW_ICONIFIED) == 1 ? true : false;

        Window.Update();
        Audio.Update();
        Assets.Update();
        ProfilingStats.Update();

        KeyBindManager.Update();
        if (Application.Playing) PhysicsManager.Update();
        MousePickingCollision.Update();

        Variables.EditorCamera.Update();

        ShadowRender();

        renderFramebuffer.Bind();
        Skybox.Render();
        SceneManager.GetCurrentScene().Render();
        RenderQueue.Render();
        RenderQueue.Clear();
        renderFramebuffer.Unbind();

        Window.GetFrameBuffer().Bind();
        PreRender();

        Lighting.UpdateUniforms();
        Skybox.Render();
        SceneManager.GetCurrentScene().Update();
        RenderQueue.Render();
        RenderQueue.Clear();
        NanoVG();

        if (!Application.Playing) {
            GridLines.Render();
            Debug.Render();

            for (Gizmo gizmo : GizmoManager.gizmos) {
                gizmo.Update();
            }
        }

        Window.GetFrameBuffer().Unbind();
        //NanoVG();
        PostProcessing.Render(false);

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
        NodeScripting.Render();

        ImGui.end();
    }

    private static void PreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
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

    private static void NanoVG() {
        //NanoVGGL3.nvgluBindFramebuffer(NVG.Instance, NVG.Framebuffer);
        NanoVG.nvgBeginFrame(NVG.Instance, 1920, 1080, 1.0f);

        NVG.Render();

        NanoVG.nvgEndFrame(NVG.Instance);
        //NanoVGGL3.nvgluBindFramebuffer(NVG.Instance, null);
    }

    private static void ShadowRender() {
        DepthFramebuffer.DepthTesting = true;
        GL11.glViewport(0, 0, Shadows.ShadowFramebufferSize, Shadows.ShadowFramebufferSize);
        Shadows.framebuffer.Bind();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        SceneManager.GetCurrentScene().Render();
        RenderQueue.Render();
        RenderQueue.Clear();
        Shadows.framebuffer.Unbind();
        DepthFramebuffer.DepthTesting = false;
        GL11.glViewport(0, 0, 1920, 1080);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    private static void Initialize() {
        NVG.Initialize();
        Component.Initialize();

        Editor.Initialize();
        MenuBar.Initialize();
        Viewport.Initialize();
        ProjectExplorer.Initialize();
        ProfilingStats.Initialize();
        Inspector.Initialize();
        NodeScripting.Initialize();

        EditorRenderer.Initialize();
        GridLines.Initialize();
        MousePickingCollision.Initialize();

        Skybox.Initialize();

        KeyBindManager.Initialize();
        PhysicsManager.Initialize();
        PostProcessing.Initialize();
    }

}
