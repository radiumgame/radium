package Editor;

import Engine.Input.Input;
import Engine.Util.NonInstantiatable;
import imgui.ImGui;
import imgui.flag.ImGuiKey;
import org.lwjgl.glfw.GLFW;

import java.util.Hashtable;
import java.util.List;

public final class KeyBindManager extends NonInstantiatable {

    private static Hashtable<int[], Runnable> keybindActions = new Hashtable<>();

    public static void RegisterKeybind(int[] keys, Runnable action) {
        keybindActions.put(keys, action);
    }

    public static void Update() {
        for (int[] keys : keybindActions.keySet()) {
            boolean runAction = true;
            for (int key : keys) {
                if (!Input.GetKey(key)) {
                    runAction = false;
                }
            }

            if (runAction) keybindActions.get(keys).run();
        }
    }

}
