package RadiumEditor;

import Radium.Math.Transform;

/**
 * Editor state object, contains open scene, editor camera transform, and other settings
 */
public class EditorState {

    /**
     * Currently open scene
     */
    public String openScene;
    /**
     * Editor camera transform
     */
    public Transform editorCameraTransform;
    /**
     * All skybox cube map textures
     */
    public String[] skybox;

}