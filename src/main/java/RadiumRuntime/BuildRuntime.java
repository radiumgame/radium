package RadiumRuntime;

import Radium.Objects.GameObject;
import Radium.PostProcessing.PostProcessing;
import Radium.SceneManagement.Scene;
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
import Radium.SceneManagement.SceneManager;
import RadiumEditor.*;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/**
 * The main application, starts the program
 */
public class BuildRuntime {

    /**
     * Starts program
     * @param args
     */
    public static void main(String[] args) {
        Start();
    }

    private static float fps = 1;
    private static long fpsTime;

    private static Settings settings;

    protected BuildRuntime() {}

    private static void Start() {
        Window.CreateWindow(1920, 1080, "Radium3D", false);
        Window.SetIcon("EngineAssets/Textures/Icon/icondark.png");
        Window.Maximize();

        Renderers.Initialize();
        UIRenderer.Initialize();
        Lighting.Initialize();
        Shadows.CreateFramebuffer();

        Variables.EditorCamera = new EditorCamera();
        Variables.EditorCamera.transform.position = new Vector3(-4f, 1.5f, 4f);
        Variables.EditorCamera.transform.rotation = new Vector3(15, 45, 0);

        settings = Settings.TryLoadSettings("EngineAssets/editor.settings");
        Variables.Settings = settings;
        settings.Enable();

        Initialize();

        Application application = new Application();
        application.Initialize();

        EventSystem.Trigger(null, new Event(EventType.Load));
        EventSystem.Trigger(null, new Event(EventType.Play));

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
        Window.Update();
        Audio.Update();
        PhysicsManager.Update();
        ShadowRender();

        Window.GetFrameBuffer().Bind();
        PreRender();

        Lighting.UpdateUniforms();
        SceneManager.GetCurrentScene().Update();
        Skybox.Render();

        PostProcessing.Render(true);

        Window.GetFrameBuffer().Unbind();
        PostRender();
    }

    private static void PreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private static void PostRender() {
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
        Component.Initialize();
        Skybox.Initialize();
        PhysicsManager.Initialize();
        PostProcessing.Initialize();
    }

}
