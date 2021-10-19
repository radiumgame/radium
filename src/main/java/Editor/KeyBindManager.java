package Editor;

import Engine.Input.Input;
import Engine.Objects.GameObject;
import Engine.SceneManagement.SceneManager;
import Engine.Util.NonInstantiatable;
import imgui.ImGui;
import imgui.flag.ImGuiKey;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public final class KeyBindManager extends NonInstantiatable {

    private static Hashtable<int[], Runnable> keybindActions = new Hashtable<>();
    private static List<Boolean> keybindDown = new ArrayList<>();

    private static GameObject currentCopy;

    public static void RegisterKeybind(int[] keys, Runnable action) {
        keybindActions.put(keys, action);
        keybindDown.add(false);
    }

    public static void Initialize() {
        RegisterKeybind(new int[] { GLFW.GLFW_KEY_DELETE }, () -> {
            if (SceneHierarchy.current != null) {
                SceneHierarchy.current.Destroy();
                SceneHierarchy.current = null;
            }
        });

        RegisterKeybind(new int[] { GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_C }, () -> {
            if (SceneHierarchy.current != null) {
                currentCopy = SceneHierarchy.current.Clone();
                currentCopy.name = currentCopy.name + " (Clone)";
            }
        });

        RegisterKeybind(new int[] { GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_X }, () -> {
            if (SceneHierarchy.current != null) {
                currentCopy = SceneHierarchy.current.Clone();
                currentCopy.name = currentCopy.name + " (Clone)";

                SceneHierarchy.current.Destroy();
            }
        });

        RegisterKeybind(new int[] { GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_V }, () -> {
            if (currentCopy != null) {
                SceneManager.GetCurrentScene().gameObjectsInScene.add(currentCopy.Clone());
            }
        });
    }

    public static void Update() {
        int index = 0;
        for (int[] keys : keybindActions.keySet()) {
            boolean runAction = true;
            for (int key : keys) {
                if (!Input.GetKey(key)) {
                    runAction = false;
                }
            }

            if (runAction && !keybindDown.get(index)) {
                keybindActions.get(keys).run();
                keybindDown.set(index, true);
            } else if (!runAction && keybindDown.get(index)) {
                keybindDown.set(index, false);
            }
            index++;
        }
    }

}
