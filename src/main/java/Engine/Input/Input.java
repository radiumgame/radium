package Engine.Input;

import Editor.Gui;
import Engine.Math.Vector.Vector2;
import Engine.Util.NonInstantiatable;
import org.lwjgl.glfw.*;

public final class Input extends NonInstantiatable {
    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] buttonsReleased = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static double mouseX, mouseY;
    private static double scrollX, scrollY;

    private static GLFWKeyCallback keyboard;
    private static GLFWCursorPosCallback mouseMove;
    private static GLFWMouseButtonCallback mouseButtons;
    private static GLFWScrollCallback scroll;

    public static void Initialize() {
        keyboard = new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == -1) return;

                keys[key] = (action != GLFW.GLFW_RELEASE);

                Gui.SetupKeyboard(window, key, scancode, action, mods);
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
                if (button == -1) return;

                buttons[button] = (action != GLFW.GLFW_RELEASE);
                buttonsReleased[button] = (action == GLFW.GLFW_RELEASE);

                Gui.SetupMouse(window, button, action, mods);
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

    public static void ResetRelease() {
        for (int i = 0; i < buttonsReleased.length; i++) {
            buttonsReleased[i] = false;
        }
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

    public static GLFWScrollCallback GetMouseScrollCallback() { return scroll; }
}
