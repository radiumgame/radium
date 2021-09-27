package Engine;

import Engine.Math.Vector.Vector2;
import org.lwjgl.glfw.*;

public final class Input {
    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] buttonsReleased = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static double mouseX, mouseY;
    private static double scrollX, scrollY;

    private static GLFWKeyCallback keyboard;
    private static GLFWCursorPosCallback mouseMove;
    private static GLFWMouseButtonCallback mouseButtons;

    public Input() {
        throw new UnsupportedOperationException("Cannot instantiate Input class");
    }

    public static void Initialize() {
        keyboard = new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keys[key] = (action != GLFW.GLFW_RELEASE);
            }
        };

        mouseMove = new GLFWCursorPosCallback() {
            public void invoke(long window, double xpos, double ypos) {
                mouseX = xpos;
                mouseY = ypos;
            }
        };

        mouseButtons = new GLFWMouseButtonCallback() {
            public void invoke(long window, int button, int action, int mods) {
                buttons[button] = (action != GLFW.GLFW_RELEASE);
                buttonsReleased[button] = (action == GLFW.GLFW_RELEASE);
            }
        };
    }

    public static void ScrollCallback(long window, double offsetx, double offsety) {
        scrollX += offsetx;
        scrollY += offsety;
    }

    public static boolean GetKey(int key) {
        return keys[key];
    }

    public static boolean GetMouseButton(int button) {
        return buttons[button];
    }

    public static boolean GetMouseButtonReleased(int button) { return buttonsReleased[button]; }

    public static void SetMouseButtonReleasedFalse(int button) {
        buttonsReleased[button] = false;
    }

    public static void Destroy() {
        keyboard.free();
        mouseMove.free();
        mouseButtons.free();
    }

    public static double GetMouseX() {
        return mouseX;
    }

    public static double GetMouseY() {
        return mouseY;
    }

    public static double GetScrollX() {
        return scrollX;
    }

    public static double GetScrollY() {
        return scrollY;
    }

    public static void ResetScroll() {
        scrollX = 0;
        scrollY = 0;
    }

    public static Vector2 GetMousePosition() {
        return new Vector2((float)GetMouseX(), (float)GetMouseY());
    }

    public static GLFWKeyCallback GetKeyboardCallback() {
        return keyboard;
    }

    public static GLFWCursorPosCallback GetMouseMoveCallback() {
        return mouseMove;
    }

    public static GLFWMouseButtonCallback GetMouseButtonsCallback() {
        return mouseButtons;
    }
}
