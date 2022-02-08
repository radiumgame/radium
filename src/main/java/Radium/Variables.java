package Radium;

import RadiumEditor.Settings;
import Radium.Components.Rendering.Camera;
import Radium.Objects.EditorCamera;

/**
 * Contains important variables such as cameras and editor settings
 */
public class Variables {

    protected Variables() {}

    /**
     * Default camera used for rendering
     */
    public static Camera DefaultCamera;

    /**
     * Camera used for edit mode in editor
     */
    public static EditorCamera EditorCamera;

    /**
     * Editor settings
     */
    public static Settings Settings;

}
