package Runtime;

import Editor.Editor;
import Editor.Gui;
import Engine.*;
import Engine.EventSystem.EventSystem;
import Engine.EventSystem.Events.Event;
import Engine.EventSystem.Events.EventType;
import Engine.Gizmo.Gizmo;
import Engine.Gizmo.GizmoManager;
import Engine.Graphics.Lighting;
import Engine.Graphics.Renderers.EditorRenderer;
import Engine.Graphics.Renderers.LitRenderer;
import Engine.Graphics.Texture;
import Engine.Objects.GameObject;
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

    private static GameObject camera;
    private static GameObject cube;

    private static float fps = 1;
    private static long fpsTime;

    private static void Start() {
        Window.CreateWindow(1920, 1080, "Radium3D");
        Window.SetIcon("EngineAssets/Textures/icondark.png");
        Window.Maximize();

        Editor.Initialize();
        Inspector.Initialize();
        EditorRenderer.Initialize();
        Skybox.Initialize();
        Skybox.SetSkyboxTexture(new Texture("EngineAssets/Textures/Skybox.jpg"));

        Variables.LitRenderer = new LitRenderer();
        Lighting.Initialize();

        SceneManager.SwitchScene(new Scene("EngineAssets/Scenes/demo.radiumscene"));
        SceneManager.GetCurrentScene().Load();

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

        SceneManager.GetCurrentScene().Save();
        EventSystem.Trigger(null, new Event(EventType.Exit));
    }

    private static void Update() {
        Window.Update();

        Window.GetFrameBuffer().Bind();

        PreRender();

        Lighting.UpdateUniforms();
        SceneManager.GetCurrentScene().Update();
        Skybox.Render();

        for (Gizmo gizmo : GizmoManager.gizmos) {
            gizmo.Update();
        }

        Window.GetFrameBuffer().Unbind();

        RenderGUI();
        Editor.RenderEditorWindows();

        PostRender();
    }

    private static void RenderGUI() {
        Editor.SetupDockspace();

        Editor.Viewport();
        MenuBar.RenderMenuBar();
        SceneHierarchy.Render();
        Inspector.Render();
        Console.Render();
        Preferences.Render();

        ImGui.end();
    }

    private static void PreRender() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();

        Gui.StartFrame();
        ImGui.newFrame();
    }

    private static void PostRender() {
        ImGui.render();
        Gui.EndFrame();

        GLFW.glfwPollEvents();
        Window.SwapBuffers();
    }

}
