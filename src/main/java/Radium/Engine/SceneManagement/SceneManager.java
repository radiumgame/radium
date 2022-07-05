package Radium.Engine.SceneManagement;

import Radium.Integration.Discord.DiscordStatus;
import Radium.Engine.Variables;
import Radium.Engine.Window;

import Radium.Editor.SceneHierarchy;
import Radium.Runtime;

/**
 * Manages switching scenes and current scene
 */
public class SceneManager {

    /**
     * Currently open scene
     */
    private static Scene currentScene;

    protected SceneManager() {}

    /**
     * Switch the current scene to a new scene
     * @param scene
     */
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

        if (Runtime.title == "RadiumEngine") Window.SetWindowTitle("Radium | " + scene.file.getName());
        else Window.SetWindowTitle(Runtime.title);
    }

    /**
     * Returns the currently open scene
     * @return currentScene variable
     */
    public static Scene GetCurrentScene() {
        return currentScene;
    }

}
