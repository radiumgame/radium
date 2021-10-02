package Engine.SceneManagement;

import Engine.Util.NonInstantiatable;
import Engine.Window;

public final class SceneManager extends NonInstantiatable {

    private static Scene currentScene;

    public static void SwitchScene(Scene scene) {
        currentScene = scene;
        Window.SetWindowTitle("Radium3D | " + scene.file.getName());
    }

    public static Scene GetCurrentScene() {
        return currentScene;
    }

}
