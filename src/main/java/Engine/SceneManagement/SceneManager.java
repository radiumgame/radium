package Engine.SceneManagement;

public class SceneManager {

    private static Scene currentScene;

    public static void SwitchScene(Scene scene) {
        currentScene = scene;
    }

    public static Scene GetCurrentScene() {
        return currentScene;
    }

}
