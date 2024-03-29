package Radium;

import Radium.Editor.Files.FileSelector;
import Radium.Editor.Files.Parser;
import Radium.Editor.NodeScripting.NodeScripting;
import Radium.Engine.FrustumCulling.FrustumFilter;
import Radium.Engine.Input.Input;
import Radium.Engine.Input.Keys;
import Radium.Engine.Skybox.Skybox;
import Radium.Integration.Project.Assets;
import Radium.Integration.Project.Project;
import Radium.Engine.Components.Rendering.Light;
import Radium.Engine.Graphics.RenderQueue;
import Radium.Engine.Graphics.Renderers.MousePickingRenderer;
import Radium.Engine.PostProcessing.PostProcessing;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.UI.NanoVG.NVG;
import Radium.Editor.Debug.Debug;
import Radium.Editor.Editor;
import Radium.Editor.EditorWindows.ThemeEditor;
import Radium.Editor.Gui;
import Radium.Engine.*;
import Radium.Editor.Debug.GridLines;
import Radium.Engine.EventSystem.EventSystem;
import Radium.Engine.EventSystem.Events.Event;
import Radium.Engine.EventSystem.Events.EventType;
import Radium.Editor.Debug.Gizmo.Gizmo;
import Radium.Editor.Debug.Gizmo.GizmoManager;
import Radium.Engine.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Engine.Graphics.Lighting.Lighting;
import Radium.Engine.Graphics.Renderers.EditorRenderer;
import Radium.Engine.Graphics.Renderers.Renderers;
import Radium.Engine.Graphics.Shadows.Shadows;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.EditorCamera;
import Radium.Engine.Physics.PhysicsManager;
import Radium.Engine.SceneManagement.SceneManager;
import Radium.Editor.*;
import Radium.Editor.Im3D.Im3D;
import Radium.Editor.ImNotify.ImNotify;
import Radium.Editor.MousePicking.MousePicking;
import Radium.Editor.Profiling.ProfilingStats;
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
        String path = null;
        if (args.length > 0) {
            path = args[0];
        }
        Start(path);
    }

    private static float fps = 1;
    private static long fpsTime;

    private static final float GarbageCollectTime = 3;
    private static float time;

    private static final String Image = "EngineAssets/Textures/Icon/bootup.png";

    /**
     * Window title
     */
    public static String title = "Radium Engine";
    private static boolean Minimized;

    protected Runtime() {}

    private static void Start(String directory) {
        if (directory == null) directory = FileExplorer.ChooseDirectory();

        if (!FileExplorer.IsPathValid(directory)) {
            System.exit(0);
        }
        new Project(directory);
        Parser.ParseAll();

        CreatingEngine.OpenWindowMultiThread(Image);

        Window.CreateWindow(1600, 900, title, true);
        Window.SetIcon("EngineAssets/Textures/Icon/icon.png");
        Window.Show();

        Variables.Settings = Settings.TryLoadSettings("EngineAssets/editor.settings");

        PreInitialize();

        Variables.EditorCamera = new EditorCamera();
        Variables.EditorCamera.transform.position = new Vector3(-4f, 1.5f, 4f);
        Variables.EditorCamera.transform.rotation = new Vector3(15, 45, 0);

        Application application = new Application();
        application.Initialize();

        Project.Current().ApplyConfiguration();

        Initialize();
        EventSystem.Trigger(null, new Event(EventType.Load));
        Variables.Settings.Enable();

        CreatingEngine.CloseWindowMultiThread();

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

            time += Time.deltaTime;
            if (time > GarbageCollectTime) {
                System.gc();
                time = 0;
            }
        }
        Project.Current().SaveConfiguration();
        EventSystem.Trigger(null, new Event(EventType.Exit));

        Window.Destroy();
    }

    private static void Update() {
        if (Input.GetKey(Keys.Escape)) {
            Input.UnhideCursor();
        }

        OGLCommands.RunCommands();

        Minimized = GLFW.glfwGetWindowAttrib(Window.GetRaw(), GLFW.GLFW_ICONIFIED) == 1;

        Window.Update();
        Assets.Update();
        ProfilingStats.Update();

        KeyBindManager.Update();
        if (Application.Playing) PhysicsManager.Update();

        Variables.EditorCamera.Update();
        Im3D.Update();

        ShadowRender();
        Window.GetMultisampledFrameBuffer().Bind();

        PreRender();

        FrustumFilter.UpdateFrustum();
        Skybox.Render();
        SceneManager.GetCurrentScene().Update();
        RenderQueue.Render();
        RenderQueue.Clear();

        //NanoVG();

        if (!Application.Playing) {
            if (LocalEditorSettings.Grid) GridLines.Render();
            Debug.Render();

            for (Gizmo gizmo : GizmoManager.gizmos) {
                gizmo.Update();
            }
        } else {
            NanoVG();
        }

        Window.Multisample();
        Window.GetMultisampledFrameBuffer().Unbind();

        MousePicking.Render();
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
        ThemeEditor.Render();
        ProjectSettings.Render();
        FileSelector.Render();
        NodeScripting.Render();
        EditorGUI.UpdateHover();

        ImGui.end();
        ImNotify.renderNotifications();
    }

    private static void PreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

        if (!Minimized) {
            Gui.StartFrame();
            ImGui.newFrame();

            ImGui.getIO().setFramerate(60);
        }
    }

    private static void PostRender() {
        if (!Minimized) {
            ImGui.render();
            Gui.EndFrame();
        }

        Input.Update();
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

    public static boolean DoDepthTest = false;
    private static void ShadowRender() {
        if (!DoDepthTest) return;
        DepthFramebuffer.DepthTesting = true;
        GL11.glViewport(0, 0, Shadows.ShadowFramebufferSize, Shadows.ShadowFramebufferSize);

        if (Light.lightsInScene.size() > 0) {
            Light.lightsInScene.get(0).DepthTest();
        }

        DepthFramebuffer.DepthTesting = false;
        GL11.glViewport(0, 0, Window.width, Window.height);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        DoDepthTest = false;
    }

    private static void PreInitialize() {
        Renderers.Initialize();
        Lighting.Initialize();
        Shadows.Initialize();
        PhysicsManager.Initialize();
        PostProcessing.Initialize();
        NVG.Initialize();
        Parser.LoadImages();
    }

    private static void Initialize() {
        Icons.Initialize();
        Component.Initialize();
        Editor.Initialize();
        MenuBar.Initialize();
        Console.Initialize();
        Viewport.Initialize();
        ProjectExplorer.Initialize();
        SceneHierarchy.Initialize();
        ProfilingStats.Initialize();
        Inspector.Initialize();
        EditorGUI.InitializeIcons();
        MousePickingRenderer.Initialize();
        MousePicking.Initialize();
        Preferences.Initialize();
        FileSelector.Initialize();
        NodeScripting.Initialize();

        ImNotify.initialize(Gui.notificationFont);
        Im3D.Initialize();

        EditorRenderer.Initialize();
        GridLines.Initialize();

        Skybox.Initialize();
        FrustumFilter.Initialize();
        Lighting.UpdateUniforms();

        KeyBindManager.Initialize();

        for (Light light : Light.lightsInScene) {
            light.Init();
        }
    }

}
