package Radium.Engine.Input;

import Radium.Editor.Console;
import Radium.Editor.Gui;
import Radium.Editor.Viewport;
import Radium.Engine.Application;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Util.KeyUtility;
import Radium.Engine.Window;
import org.lwjgl.glfw.*;

/**
 * Input detection
 */
public class Input {

    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] keysPressed = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] keysReleased = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] buttonsReleased = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] buttonsPressed = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static double mouseX, mouseY;
    private static double mouseDeltaX, mouseDeltaY;
    private static double scrollX, scrollY;

    private static GLFWKeyCallback keyboard;
    private static GLFWCursorPosCallback mouseMove;
    private static GLFWMouseButtonCallback mouseButtons;
    private static GLFWScrollCallback scroll;

    protected Input() {}

    /**
     * Initialize the keyboard callbacks
     */
    public static void Initialize() {
        keyboard = new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == -1) return;

                keys[key] = (action != GLFW.GLFW_RELEASE);
                keysReleased[key] = (action == GLFW.GLFW_RELEASE);
                keysPressed[key] = (action == GLFW.GLFW_PRESS);

                Gui.SetupKeyboard(window, key, scancode, action, mods);
            }
        };

        mouseMove = new GLFWCursorPosCallback() {
            public void invoke(long window, double xpos, double ypos) {
                mouseDeltaX = xpos - mouseX;
                mouseDeltaY = ypos - mouseY;
                mouseX = xpos;
                mouseY = ypos;
            }
        };

        mouseButtons = new GLFWMouseButtonCallback() {
            public void invoke(long window, int button, int action, int mods) {
                if (button == -1) return;

                buttons[button] = (action != GLFW.GLFW_RELEASE);
                buttonsReleased[button] = (action == GLFW.GLFW_RELEASE);
                buttonsPressed[button] = (action == GLFW.GLFW_PRESS);

                Gui.SetupMouse(window, button, action, mods);
            }
        };
    }

    public static void Update() {
        for (int i = 0; i < keysReleased.length; i++) {
            keysReleased[i] = false;
            keysPressed[i] = false;
        }
        for (int i = 0; i < buttonsReleased.length; i++) {
            buttonsReleased[i] = false;
            buttonsPressed[i] = false;
        }

        if (cursorHidden) {
            GLFW.glfwSetInputMode(Window.GetRaw(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }
    }

    public static void ScrollCallback(long window, double offsetx, double offsety) {
        scrollX += offsetx;
        scrollY += offsety;
    }

    /**
     * Returns if key is pressed
     * @param key Key
     * @return If key is pressed
     */
    public static boolean GetKey(Keys key) {
        int glfw = KeyUtility.GLFWFromKeys(key);
        if (glfw == -1) return false;

        return keys[glfw];
    }

    public static boolean GetKeyPressed(Keys key) {
        int glfw = KeyUtility.GLFWFromKeys(key);
        if (glfw == -1) return false;

        return keysPressed[glfw];
    }

    public static boolean GetKeyReleased(Keys key) {
        int glfw = KeyUtility.GLFWFromKeys(key);
        if (glfw == -1) return false;

        return keysReleased[glfw];
    }

    private static boolean cursorHidden = false;
    public static void HideCursor() {
        if (Application.Editor && !Viewport.ViewportHovered) {
            return;
        }

        cursorHidden = true;
        GLFW.glfwSetInputMode(Window.GetRaw(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    public static void UnhideCursor() {
        cursorHidden = false;
        GLFW.glfwSetInputMode(Window.GetRaw(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

    /**
     * Returns if mouse button is pressed
     * @param button Mouse button
     * @return If mouse button is pressed
     */
    public static boolean GetMouseButton(int button) {
        return buttons[button];
    }

    /**
     * If the mouse button is released
     * @param button Mouse button
     * @return If mouse button is released
     */
    public static boolean GetMouseButtonReleased(int button) { return buttonsReleased[button]; }

    public static boolean GetMouseButtonPressed(int button) { return buttonsPressed[button]; }

    /**
     * Destroys all callbacks
     */
    public static void Destroy() {
        keyboard.free();
        mouseMove.free();
        mouseButtons.free();
    }

    /**
     * @return Mouse X position
     */
    public static double GetMouseX() {
        return mouseX;
    }

    /**
     * @return Mouse Y position
     */
    public static double GetMouseY() {
        return mouseY;
    }

    public static double GetMouseDeltaX() {
        return mouseDeltaX;
    }

    public static double GetMouseDeltaY() {
        return mouseDeltaY;
    }

    /**
     * @return Mouse Scroll X
     */
    public static double GetScrollX() {
        return scrollX;
    }

    /**
     * @return Mouse Scroll Y
     */
    public static double GetScrollY() {
        return scrollY;
    }

    /**
     * Resets the scroll values to 0
     */
    public static void ResetScroll() {
        scrollX = 0;
        scrollY = 0;
    }

    public static int StringToButton(String button) {
        if (button.equals("left")) {
            return GLFW.GLFW_MOUSE_BUTTON_LEFT;
        } else if (button.equals("right")) {
            return GLFW.GLFW_MOUSE_BUTTON_RIGHT;
        } else if (button.equals("middle")) {
            return GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
        } else {
            Console.Error("Invalid mouse button: " + button);
            return -1;
        }
    }

    /**
     * @return Mouse position on screen
     */
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
