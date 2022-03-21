package RadiumEditor;

import Radium.SceneManagement.Scene;
import Radium.SceneManagement.SceneManager;
import Radium.Skybox;
import Radium.Variables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Saves {@link EditorState editor state} object
 */
public class EditorSave {

    protected EditorSave() {}

    private static final String STATE_FILE = "EngineAssets/editor.state";

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static EditorState state;

    /**
     * Loads editor state from file
     */
    public static void LoadEditorState(boolean editor) {
        try {
            String result = new String(Files.readAllBytes(Paths.get(STATE_FILE)));
            state = gson.fromJson(result, EditorState.class);

            LoadAttributes(editor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves editor state to file
     */
    public static void SaveEditorState() {
        EditorState newState = new EditorState();

        newState.openScene = SceneManager.GetCurrentScene().file.getAbsolutePath();
        newState.editorCameraTransform = Variables.EditorCamera.transform;
        newState.skybox = Skybox.textures;

        String json = gson.toJson(newState);

        try {
            FileWriter writer = new FileWriter(STATE_FILE);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    private static void LoadAttributes(boolean editor) {
        SceneManager.SwitchScene(new Scene(state.openScene));
        if (editor) Variables.EditorCamera.transform = state.editorCameraTransform;
        Skybox.textures = state.skybox;
        Skybox.UpdateTextures();
    }

}