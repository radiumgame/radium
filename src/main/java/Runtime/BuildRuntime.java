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

public final class BuildRuntime extends NonInstantiatable {

    public static void main(String[] args) {
        Start();
    }

    private static float fps = 1;
    private static long fpsTime;

    private static void Start() {
        Window.CreateWindow(1920, 1080, "Radium3D");
        Window.SetIcon("EngineAssets/Textures/icondark.png");
        Window.Maximize();

        Renderers.Initialize();
        Lighting.Initialize();

        Skybox.Initialize();
        Skybox.SetSkyboxTexture(new Texture("EngineAssets/Textures/Skybox.jpg"));

        Application application = new Application();
        application.Initialize();
        Application.IsEditor = false;

        SceneManager.SwitchScene(new Scene("Assets/Scenes/default.radiumscene"));

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

        PreRender();

        Lighting.UpdateUniforms();
        SceneManager.GetCurrentScene().Update();
        Skybox.Render();

        PostRender();
    }

    private static void PreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();
    }

    private static void PostRender() {
        GLFW.glfwPollEvents();
        Window.SwapBuffers();
    }

}
