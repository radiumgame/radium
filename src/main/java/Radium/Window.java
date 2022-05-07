package Radium;

import Radium.Math.Vector.Vector2;
import Radium.UI.NanoVG.NVG;
import RadiumEditor.Console;
import RadiumEditor.Gui;
import Radium.Audio.Audio;
import Radium.Graphics.Framebuffer.FrameBufferTexture;
import Radium.Graphics.Framebuffer.Framebuffer;
import Radium.Input.Input;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.*;

/**
 * Basic window functionality
 */
public class Window {

    /**
     * Window width
     */
    public static int width;
    /**
     * Window height
     */
    public static int height;
    private static String title;
    private static boolean vsync = true;
    private static GLFWWindowSizeCallback windowSize;
    private static boolean isResized = false;

    /**
     * The monitor width
     */
    public static int monitorWidth;
    /**
     * Monitor height
     */
    public static int monitorHeight;

    public static Vector2 ContentScale = Vector2.Zero();

    private static long window;

    private static Framebuffer frameBuffer;

    protected Window() {}

    /**
     * Returns the window ID
     */
    public static long GetRaw() {
        return window;
    }

    /**
     * Creates a window from the parameters
     * @param Width Window width
     * @param Height Window height
     * @param Title Window title
     */
    public static void CreateWindow(int Width, int Height, String Title, boolean frameCap) {
        width = Width;
        height = Height;
        title = Title;
        vsync = frameCap;

        if (!GLFW.glfwInit()) {
            Console.Log("Couldn't initialize GLFW");
            return;
        }

        Input.Initialize();
        Audio.Initialize();
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        window = GLFW.glfwCreateWindow(width, height, title, 0, 0);

        if (window == 0) {
            Console.Log("Couldn't create window");
            return;
        }

        GLFWVidMode vm = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        monitorWidth = vm.width();
        monitorHeight = vm.height();

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL13C.GL_MULTISAMPLE);
        GL11.glEnable(GL30C.GL_FRAMEBUFFER_SRGB);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glCullFace(GL11.GL_BACK);

        windowSize = new GLFWWindowSizeCallback() {
            
            public void invoke(long window, int w, int h) {
                width = w;
                height = h;

                isResized = true;
            }
        };

        GLFW.glfwSetKeyCallback(window, Input.GetKeyboardCallback());
        GLFW.glfwSetCursorPosCallback(window, Input.GetMouseMoveCallback());
        GLFW.glfwSetMouseButtonCallback(window, Input.GetMouseButtonsCallback());
        GLFW.glfwSetScrollCallback(window, Input.GetMouseScrollCallback());
        GLFW.glfwSetWindowSizeCallback(window, windowSize);
        GLFW.glfwSetWindowContentScaleCallback(window, (handle, x, y) -> {
            ContentScale = new Vector2(x, y);
        });

        GLFW.glfwShowWindow(window);

        if (vsync)
            GLFW.glfwSwapInterval(1);
        else
            GLFW.glfwSwapInterval(0);

        frameBuffer = new Framebuffer(1920, 1080);

        GL11.glViewport(0, 0, 1920, 1080);
        Gui.Initialize(window);
    }

    /**
     * Updates resize callback and ImGui
     */
    public static void Update() {
        if (isResized) {
            ImGui.getIO().setDisplaySize(width, height);
            isResized = false;
        }
    }

    /**
     * Closes the window
     */
    public static void Close() {
        GLFW.glfwSetWindowShouldClose(window, true);
    }

    /**
     * Destroys Input, Audio, IMGUI, window, and GLFW
     */
    public static void Destroy() {
        Input.Destroy();
        Audio.Destroy();
        NVG.Destroy();
        Gui.DestroyImGui();
        GLFW.glfwWindowShouldClose(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();

        System.exit(0);
    }

    /**
     * Sets the window title
     * @param title New title
     */
    public static void SetWindowTitle(String title) {
        GLFW.glfwSetWindowTitle(window, title);
    }

    /**
     * Updates the window interaction
     */
    public static void SwapBuffers() {
        GLFW.glfwSwapBuffers(window);
    }

    /**
     * Whether the window is open
     * @return GLFW.glfwWindowShouldClose(window)
     */
    public static boolean ShouldClose() { return GLFW.glfwWindowShouldClose(window); }

    /**
     * Sets fullscreen settings
     * @param mode fullscreen = mode;
     */
    public static void SetFullscreen(boolean mode) {
        int[] x = new int[1];
        int[] y = new int[1];
        GLFW.glfwGetWindowPos(window, x, y);

        GLFW.glfwSetWindowMonitor(window, mode ? GLFW.glfwGetPrimaryMonitor() : 0, x[0], y[0], width, height, -1);
    }

    /**
     * Maximizes the window
     */
    public static void Maximize() {
        GLFW.glfwMaximizeWindow(window);
    }

    /**
     * Sets the windows icon
     * @param path Image path
     */
    public static void SetIcon(String path) {
        FrameBufferTexture iconTexture = new FrameBufferTexture(path);
        GLFWImage icon = GLFWImage.malloc();
        GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
        icon.set(iconTexture.width, iconTexture.height, iconTexture.GetBuffer());
        iconBuffer.put(0, icon);

        GLFW.glfwSetWindowIcon(window, iconBuffer);
    }

    /**
     * Returns the windows framebuffer
     * @return Window framebuffer
     */
    public static Framebuffer GetFrameBuffer() {
        return frameBuffer;
    }

}