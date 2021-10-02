package Engine.SceneManagement;

import Engine.Util.NonInstantiatable;

public final class SceneManager extends NonInstantiatable {

    private static Scene currentScene;

    public static void SwitchScene(Scene scene) {
        currentScene = scene;
    }

    public static Scene GetCurrentScene() {
        return currentScene;
    }

}
