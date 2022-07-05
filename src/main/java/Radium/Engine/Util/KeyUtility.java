package Radium.Engine.Util;

import Radium.Engine.Input.Keys;
import org.lwjgl.glfw.GLFW;

/**
 * Utility for converting Radium input to GLFW input
 */
public class KeyUtility {

    /**
     * Converts {@link Keys key} to GLFW input index
     * @return Keys -> GLFW input index
     */
    public static int GLFWFromKeys(Keys key) {
        int glfw = -1;

        switch (key) {
            case A -> glfw = GLFW.GLFW_KEY_A;
            case B -> glfw = GLFW.GLFW_KEY_B;
            case C -> glfw = GLFW.GLFW_KEY_C;
            case D -> glfw = GLFW.GLFW_KEY_D;
            case E -> glfw = GLFW.GLFW_KEY_E;
            case F -> glfw = GLFW.GLFW_KEY_F;
            case G -> glfw = GLFW.GLFW_KEY_G;
            case H -> glfw = GLFW.GLFW_KEY_H;
            case I -> glfw = GLFW.GLFW_KEY_I;
            case J -> glfw = GLFW.GLFW_KEY_J;
            case K -> glfw = GLFW.GLFW_KEY_K;
            case L -> glfw = GLFW.GLFW_KEY_L;
            case M -> glfw = GLFW.GLFW_KEY_M;
            case N -> glfw = GLFW.GLFW_KEY_N;
            case O -> glfw = GLFW.GLFW_KEY_O;
            case P -> glfw = GLFW.GLFW_KEY_P;
            case Q -> glfw = GLFW.GLFW_KEY_Q;
            case R -> glfw = GLFW.GLFW_KEY_R;
            case S -> glfw = GLFW.GLFW_KEY_S;
            case T -> glfw = GLFW.GLFW_KEY_T;
            case U -> glfw = GLFW.GLFW_KEY_U;
            case V -> glfw = GLFW.GLFW_KEY_V;
            case W -> glfw = GLFW.GLFW_KEY_W;
            case X -> glfw = GLFW.GLFW_KEY_X;
            case Y -> glfw = GLFW.GLFW_KEY_Y;
            case Z -> glfw = GLFW.GLFW_KEY_Z;
            case F1 -> glfw = GLFW.GLFW_KEY_F1;
            case F2 -> glfw = GLFW.GLFW_KEY_F2;
            case F3 -> glfw = GLFW.GLFW_KEY_F3;
            case F4 -> glfw = GLFW.GLFW_KEY_F4;
            case F5 -> glfw = GLFW.GLFW_KEY_F5;
            case F6 -> glfw = GLFW.GLFW_KEY_F6;
            case F7 -> glfw = GLFW.GLFW_KEY_F7;
            case F8 -> glfw = GLFW.GLFW_KEY_F8;
            case F9 -> glfw = GLFW.GLFW_KEY_F9;
            case F10 -> glfw = GLFW.GLFW_KEY_F10;
            case F11 -> glfw = GLFW.GLFW_KEY_F11;
            case F12 -> glfw = GLFW.GLFW_KEY_F12;
            case Tab -> glfw = GLFW.GLFW_KEY_TAB;
            case CapsLock -> glfw = GLFW.GLFW_KEY_CAPS_LOCK;
            case LeftShift -> glfw = GLFW.GLFW_KEY_LEFT_SHIFT;
            case LeftCtrl -> glfw = GLFW.GLFW_KEY_LEFT_CONTROL;
            case LeftAlt -> glfw = GLFW.GLFW_KEY_LEFT_ALT;
            case Space -> glfw = GLFW.GLFW_KEY_SPACE;
            case RightAlt -> glfw = GLFW.GLFW_KEY_RIGHT_ALT;
            case RightCtrl -> glfw = GLFW.GLFW_KEY_RIGHT_CONTROL;
            case RightShift -> glfw = GLFW.GLFW_KEY_RIGHT_SHIFT;
            case Enter -> glfw = GLFW.GLFW_KEY_ENTER;
            case Slash -> glfw = GLFW.GLFW_KEY_SLASH;
            case BackSlash -> glfw = GLFW.GLFW_KEY_BACKSLASH;
            case Escape -> glfw = GLFW.GLFW_KEY_ESCAPE;
            case Comma -> glfw = GLFW.GLFW_KEY_COMMA;
            case Period -> glfw = GLFW.GLFW_KEY_PERIOD;
            case SemiColon -> glfw = GLFW.GLFW_KEY_SEMICOLON;
            case Insert -> glfw = GLFW.GLFW_KEY_INSERT;
            case Home -> glfw = GLFW.GLFW_KEY_HOME;
            case PageUp -> glfw = GLFW.GLFW_KEY_PAGE_UP;
            case Delete -> glfw = GLFW.GLFW_KEY_DELETE;
            case End -> glfw = GLFW.GLFW_KEY_END;
            case PageDown -> glfw = GLFW.GLFW_KEY_PAGE_DOWN;
            case Backspace -> glfw = GLFW.GLFW_KEY_BACKSPACE;
            case ArrowUp -> glfw = GLFW.GLFW_KEY_UP;
            case ArrowLeft -> glfw = GLFW.GLFW_KEY_LEFT;
            case ArrowDown -> glfw = GLFW.GLFW_KEY_DOWN;
            case ArrowRight -> glfw = GLFW.GLFW_KEY_RIGHT;
            case Number1 -> glfw = GLFW.GLFW_KEY_1;
            case Number2 -> glfw = GLFW.GLFW_KEY_2;
            case Number3 -> glfw = GLFW.GLFW_KEY_3;
            case Number4 -> glfw = GLFW.GLFW_KEY_4;
            case Number5 -> glfw = GLFW.GLFW_KEY_5;
            case Number6 -> glfw = GLFW.GLFW_KEY_6;
            case Number7 -> glfw = GLFW.GLFW_KEY_7;
            case Number8 -> glfw = GLFW.GLFW_KEY_8;
            case Number9 -> glfw = GLFW.GLFW_KEY_9;
            case Minus -> glfw = GLFW.GLFW_KEY_MINUS;
            case Equal -> glfw = GLFW.GLFW_KEY_EQUAL;
            default -> glfw = GLFW.GLFW_KEY_UNKNOWN;
        }

        return glfw;
    }

}
