package Radium.Engine.SceneManagement;

import Radium.Build;
import Radium.Integration.Discord.DiscordStatus;
import Radium.Engine.Variables;
import Radium.Engine.Window;

import Radium.Editor.SceneHierarchy;
import Radium.Integration.Project.Project;
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

        if (Build.Editor && Variables.Settings.UseDiscord) {
            DiscordStatus.UpdateScene();
        }

        Project.Current().configuration.openScene = scene.file.getAbsolutePath();
        SceneHierarchy.current = null;

        if (Build.Editor) {
            if (Runtime.title == "RadiumEngine") Window.SetWindowTitle("Radium | " + scene.file.getName());
            else Window.SetWindowTitle(Runtime.title);
        }
    }

    /**
     * Returns the currently open scene
     * @return currentScene variable
     */
    public static Scene GetCurrentScene() {
        return currentScene;
    }

}
