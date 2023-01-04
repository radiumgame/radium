package Radium;

import Radium.Editor.CreatingEngine;
import Radium.Engine.FrustumCulling.FrustumFilter;
import Radium.Engine.Input.Input;
import Radium.Engine.Skybox.Skybox;
import Radium.Integration.Project.Project;
import Radium.Engine.Components.Rendering.Light;
import Radium.Engine.Graphics.RenderQueue;
import Radium.Engine.PostProcessing.PostProcessing;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.UI.NanoVG.NVG;
import Radium.Engine.*;
import Radium.Engine.EventSystem.EventSystem;
import Radium.Engine.EventSystem.Events.Event;
import Radium.Engine.EventSystem.Events.EventType;
import Radium.Engine.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Engine.Graphics.Lighting.Lighting;
import Radium.Engine.Graphics.Renderers.Renderers;
import Radium.Engine.Graphics.Shadows.Shadows;
import Radium.Engine.Objects.EditorCamera;
import Radium.Engine.Physics.PhysicsManager;
import Radium.Engine.SceneManagement.SceneManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;

/**
 * The main application, starts the program
 */
public class Build {

    public static boolean Editor = true;

    /**
     * Starts program
     * @param args
     */
    public static void main(String[] args) {
        Editor = false;

        String path = null;
        if (args.length > 0) {
            path = args[0];
        }
        Start(path);
    }

    private static float fps = 1;
    private static long fpsTime;

    private static float time;

    private static final float GarbageCollectTime = 3;

    protected Build() {}

    private static void Start(String directory) {
        if (directory == null) directory = FileExplorer.ChooseDirectory();

        if (!FileExplorer.IsPathValid(directory)) {
            System.exit(0);
        }
        Project project = new Project(directory);
        CreatingEngine.OpenWindowMultiThread(project.configuration.projectBootup);

        Window.CreateWindow(1024, 576, project.configuration.projectName, true);
        Window.SetIcon(project.configuration.projectIcon);

        PreInitialize();

        Variables.EditorCamera = new EditorCamera();
        Variables.EditorCamera.CalculateMatrices();
        Application application = new Application();
        application.Initialize();
        Application.Editor = false;

        Project.Current().ApplyConfiguration();

        Initialize();
        EventSystem.Trigger(null, new Event(EventType.Load));
        EventSystem.Trigger(null, new Event(EventType.Play));

        CreatingEngine.CloseWindowMultiThread();
        Window.Show();

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
        OGLCommands.RunCommands();
        Window.Update();

        PhysicsManager.Update();

        ShadowRender();
        GL11.glViewport(0, 0, Window.width, Window.height);
        Window.GetMultisampledFrameBuffer().Bind();

        PreRender();

        FrustumFilter.UpdateFrustum();
        Skybox.Render();
        SceneManager.GetCurrentScene().Update();
        RenderQueue.Render();
        RenderQueue.Clear();

        NanoVG();
        Window.Multisample();
        Window.GetMultisampledFrameBuffer().Unbind();
        PostProcessing.Render(true);

        if (!HasDepthTested) {
            DoDepthTest = true;
        }

        PostRender();
    }

    private static void PreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
    }

    private static void PostRender() {
        Input.Update();
        GLFW.glfwPollEvents();
        Window.SwapBuffers();
    }

    private static void NanoVG() {
        //NanoVGGL3.nvgluBindFramebuffer(NVG.Instance, NVG.Framebuffer);
        NanoVG.nvgBeginFrame(NVG.Instance, 1920, 1080,1.0f);

        NVG.Render();

        NanoVG.nvgEndFrame(NVG.Instance);
        //NanoVGGL3.nvgluBindFramebuffer(NVG.Instance, null);
    }

    public static boolean DoDepthTest = false;
    private static boolean HasDepthTested = false;
    private static void ShadowRender() {
        if (!DoDepthTest) return;

        DepthFramebuffer.DepthTesting = true;
        GL11.glViewport(0, 0, Shadows.ShadowFramebufferSize, Shadows.ShadowFramebufferSize);

        if (Light.lightsInScene.size() > 0) {
            Light.lightsInScene.get(0).DepthTest();
        }

        DepthFramebuffer.DepthTesting = false;
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        DoDepthTest = false;
        HasDepthTested = true;
    }

    private static void PreInitialize() {
        Renderers.Initialize();
        Lighting.Initialize();
        Shadows.Initialize();
        PhysicsManager.Initialize();
        PostProcessing.Initialize();
    }

    private static void Initialize() {
        NVG.Initialize();
        Component.Initialize();

        Skybox.Initialize();
        FrustumFilter.Initialize();
        Lighting.UpdateUniforms();

        for (Light light : Light.lightsInScene) {
            light.Init();
        }
    }

}
