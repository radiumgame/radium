package Radium.Engine;

import Radium.Build;
import Radium.Editor.EditorWindows.Graphics;
import Radium.Editor.Viewport;
import Radium.Engine.Graphics.Framebuffer.MultisampledFramebuffer;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.UI.NanoVG.NVG;
import Radium.Editor.Console;
import Radium.Editor.Gui;
import Radium.Engine.Audio.Audio;
import Radium.Engine.Graphics.Framebuffer.FrameBufferTexture;
import Radium.Engine.Graphics.Framebuffer.Framebuffer;
import Radium.Engine.Input.Input;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

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
    public static boolean isResized = false;

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
    private static MultisampledFramebuffer multisampledFramebuffer;

    private static double s_xpos = 0, s_ypos = 0;
    private static int w_xsiz = 0, w_ysiz = 0;
    private static int dragState = 0;

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

        Graphics.Load();

        Input.Initialize();
        Audio.Initialize();
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 1);
        if (Build.Editor) GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
        else GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);

        GLFWVidMode vm = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        monitorWidth = vm.width();
        monitorHeight = vm.height();
        //width = monitorWidth;
        //height = monitorHeight;

        window = GLFW.glfwCreateWindow(width, height, title, 0, 0);

        if (window == 0) {
            Console.Log("Couldn't create window");
            return;
        }

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
        GL11.glDisable(GL33.GL_FRAMEBUFFER_SRGB);

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
    }

    public static void Multisample() {
        GL44.glBindFramebuffer(GL44.GL_READ_FRAMEBUFFER, multisampledFramebuffer.GetFBO());
        GL44.glBindFramebuffer(GL44.GL_DRAW_FRAMEBUFFER, frameBuffer.GetFBO());
        GL44.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL44.GL_COLOR_BUFFER_BIT, GL44.GL_NEAREST);
    }

    public static void SetSamples(int samples) {
        multisampledFramebuffer.Destroy();
        multisampledFramebuffer = new MultisampledFramebuffer(width, height, samples);
    }

    public static void Show() {
        GLFW.glfwShowWindow(window);

        if (vsync)
            GLFW.glfwSwapInterval(1);
        else
            GLFW.glfwSwapInterval(0);

        frameBuffer = new Framebuffer(Window.width, Window.height);
        multisampledFramebuffer = new MultisampledFramebuffer(Window.width, Window.height, Graphics.GetSamples());
        ResizeFramebuffer.add(frameBuffer);

        GL11.glViewport(0, 0, Window.width, Window.height);
        Gui.Initialize(window);
    }

    public static void Resize() {
        ImGui.getIO().setDisplaySize(width, height);

        for (Framebuffer fb : ResizeFramebuffer) {
            fb.Resize(width, height);
        }
        multisampledFramebuffer.Resize(width, height);
    }

    public final static List<Framebuffer> ResizeFramebuffer = new ArrayList<>();
    /**
     * Updates resize callback and ImGui
     */
    public static void Update() {
        if (isResized) {
            Resize();
            isResized = false;
        }

        if (!Build.Editor) return;
        if (GLFW.glfwGetMouseButton(window, 0) == GLFW.GLFW_PRESS && dragState == 0) {
            double[] x = new double[1];
            double[] y = new double[1];
            int[] xS = new int[1];
            int[] yS = new int[1];

            GLFW.glfwGetCursorPos(window, x, y);
            GLFW.glfwGetWindowSize(window, xS, yS);

            s_xpos = x[0];
            s_ypos = y[0];
            w_xsiz = xS[0];
            w_ysiz = yS[0];
            dragState = 1;
        }
        if (GLFW.glfwGetMouseButton(window, 0) == GLFW.GLFW_PRESS && dragState == 1) {
            double c_xpos, c_ypos;
            int w_xpos, w_ypos;
            double[] x = new double[1];
            double[] y = new double[1];
            int[] xS = new int[1];
            int[] yS = new int[1];
            GLFW.glfwGetCursorPos(window, x, y);
            GLFW.glfwGetWindowPos(window, xS, yS);

            c_xpos = x[0];
            c_ypos = y[0];
            w_xpos = xS[0];
            w_ypos = yS[0];
            if (
                    s_xpos >= 0 && s_xpos <= ((double)w_xsiz - 170) &&
                            s_ypos >= 0 && s_ypos <= 40) {
                GLFW.glfwSetWindowPos(window, w_xpos + (int)(c_xpos - s_xpos), w_ypos + (int)(c_ypos - s_ypos));
            }
            if (
                    s_xpos >= ((double)w_xsiz - 15) && s_xpos <= ((double)w_xsiz) &&
                            s_ypos >= ((double)w_ysiz - 15) && s_ypos <= ((double)w_ysiz)) {
                GLFW.glfwSetWindowSize(window, w_xsiz + (int)(c_xpos - s_xpos), w_ysiz + (int)(c_ypos - s_ypos));
                GLFW.glfwSetCursor(window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR));
            }
        }
        if (GLFW.glfwGetMouseButton(window, 0) == GLFW.GLFW_RELEASE && dragState == 1) {
            Resize();
            dragState = 0;
            GLFW.glfwSetCursor(window, GLFW.glfwCreateStandardCursor(GLFW.GLFW_CURSOR_NORMAL));
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
        boolean maximized = GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_MAXIMIZED) == GLFW.GLFW_TRUE;

        if (!maximized) {
            GLFW.glfwMaximizeWindow(window);
        } else {
            GLFW.glfwRestoreWindow(window);
        }

        Resize();
    }

    public static void Minimize() { GLFW.glfwIconifyWindow(window); }

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

    public static Vector2 GetContentScale() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer sx = stack.mallocFloat(1);
            FloatBuffer sy = stack.mallocFloat(1);

            GLFW.glfwGetWindowContentScale(window, sx, sy);
            float contentScaleX = sx.get(0);
            float contentScaleY = sy.get(0);
            return new Vector2(contentScaleX, contentScaleY);
        }
    }

    public static Vector2 GetFramebufferScale() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer fx = stack.mallocInt(1);
            IntBuffer fy = stack.mallocInt(1);

            GLFW.glfwGetFramebufferSize(window, fx, fy);
            float scaleX = fx.get(0);
            float scaleY = fy.get(0);
            return new Vector2(scaleX, scaleY);
        }
    }

    /**
     * Returns the windows framebuffer
     * @return Window framebuffer
     */
    public static Framebuffer GetFrameBuffer() {
        return frameBuffer;
    }

    public static MultisampledFramebuffer GetMultisampledFrameBuffer() {
        return multisampledFramebuffer;
    }

}