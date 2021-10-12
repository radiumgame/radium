package Engine.SceneManagement;

import Engine.Util.NonInstantiatable;
import Engine.Window;

import Runtime.Runtime;

public final class SceneManager extends NonInstantiatable {

    private static Scene currentScene;

    public static void SwitchScene(Scene scene) {
        currentScene = scene;
        if (Runtime.title == "Radium3D") Window.SetWindowTitle("Radium3D | " + scene.file.getName());
        else Window.SetWindowTitle(Runtime.title);
    }

    public static Scene GetCurrentScene() {
        return currentScene;
    }

}
