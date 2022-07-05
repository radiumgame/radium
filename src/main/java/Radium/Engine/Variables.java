package Radium.Engine;

import Radium.Editor.Settings;
import Radium.Engine.Components.Rendering.Camera;
import Radium.Engine.Objects.EditorCamera;

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
