package Runtime;

import Editor.Gui;
import Engine.Components.Camera;
import Engine.Components.Graphics.MeshFilter;
import Engine.Components.Graphics.MeshRenderer;
import Engine.Graphics.Mesh;
import Engine.Graphics.Renderer;
import Engine.Math.Vector.Vector3;
import Engine.Objects.GameObject;
import Engine.SceneManagement.Scene;
import Engine.SceneManagement.SceneManager;
import Engine.ThreadManager;
import Engine.Window;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public final class Runtime {

    public Runtime() {
        throw new UnsupportedOperationException("Cannot instantiate Runtime class");
    }

    public static void main(String[] args) {
        Start();
    }

    private static GameObject camera;
    private static GameObject cube;

    private static void Start() {
        Window.CreateWindow(1920, 1080, "Radium3D");
        Window.SetIcon("EngineAssets/Textures/icondark.png");
        Window.Maximize();

        Renderer.Init();

        SceneManager.SwitchScene(new Scene());

        camera = new GameObject();
        camera.AddComponent(new Camera());
        camera.transform.position = new Vector3(0, 0, 3);

        cube = new GameObject();
        cube.AddComponent(new MeshFilter(Mesh.Cube(1, 1, "EngineAssets/Textures/box.jpg")));
        cube.AddComponent(new MeshRenderer());

        while (!Window.ShouldClose()) {
            Update();
        }
    }

    private static void Update() {
        Window.Update();

        PreRender();

        SceneManager.GetCurrentScene().Update();

        PostRender();
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
