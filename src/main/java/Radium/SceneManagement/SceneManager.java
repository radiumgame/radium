package Radium.SceneManagement;

import Integration.Discord.DiscordStatus;
import Radium.Variables;
import Radium.Window;

import RadiumEditor.SceneHierarchy;
import RadiumRuntime.Runtime;

public class SceneManager {

    private static Scene currentScene;

    protected SceneManager() {}

    public static void SwitchScene(Scene scene) {
        if (currentScene != null) {
            currentScene.Unload();
        }

        currentScene = scene;
        currentScene.Load();

        if (Variables.Settings.UseDiscord) {
            DiscordStatus.UpdateScene();
        }

        SceneHierarchy.current = null;

        if (Runtime.title == "Radium3D") Window.SetWindowTitle("Radium3D | " + scene.file.getName());
        else Window.SetWindowTitle(Runtime.title);
    }

    public static Scene GetCurrentScene() {
        return currentScene;
    }

}
