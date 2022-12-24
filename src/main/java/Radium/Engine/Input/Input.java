package Radium.Engine.Input;

import Radium.Editor.Console;
import Radium.Editor.Gui;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Util.KeyUtility;
import org.lwjgl.glfw.*;

/**
 * Input detection
 */
public class Input {

    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static boolean[] buttonsReleased = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
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

                Gui.SetupMouse(window, button, action, mods);
            }
        };
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

    /**
     * Sets the mouse buttons released variable to false
     * @param button Mouse button
     */
    public static void SetMouseButtonReleasedFalse(int button) {
        buttonsReleased[button] = false;
    }

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
