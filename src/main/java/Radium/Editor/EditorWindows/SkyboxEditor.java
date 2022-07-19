package Radium.Editor.EditorWindows;

import Radium.Engine.Graphics.Texture;
import Radium.Engine.Skybox;
import Radium.Editor.EditorGUI;
import Radium.Editor.EditorWindow;
import java.io.File;

/**
 * Can change the skybox textures and settings
 */
public class SkyboxEditor extends EditorWindow {

    /**
     * Creates empty instance
     */
    public SkyboxEditor() {
        MenuName = "Skybox Editor";
    }

    
    public void Start() {

    }

    private final String[] allowedTypes = new String[] { "png", "jpg", "bmp" };
    public void RenderGUI() {
        File top = EditorGUI.FileReceive(allowedTypes, "Image", Skybox.individualTextures[2]);
        File bottom = EditorGUI.FileReceive(allowedTypes, "Image", Skybox.individualTextures[3]);
        File front = EditorGUI.FileReceive(allowedTypes, "Image", Skybox.individualTextures[5]);
        File back = EditorGUI.FileReceive(allowedTypes, "Image", Skybox.individualTextures[4]);
        File left = EditorGUI.FileReceive(allowedTypes, "Image", Skybox.individualTextures[0]);
        File right = EditorGUI.FileReceive(allowedTypes, "Image", Skybox.individualTextures[1]);

        if (top != null) {
            Skybox.textures[2] = top.getPath();
            Skybox.UpdateTextures();
        } else if (bottom != null) {
            Skybox.textures[3] = bottom.getPath();
            Skybox.UpdateTextures();
        } else if (front != null) {
            Skybox.textures[5] = front.getPath();
            Skybox.UpdateTextures();
        } else if (back != null) {
            Skybox.textures[4] = back.getPath();
            Skybox.UpdateTextures();
        } else if (left != null) {
            Skybox.textures[0] = left.getPath();
            Skybox.UpdateTextures();
        } else if (right != null) {
            Skybox.textures[1] = right.getPath();
            Skybox.UpdateTextures();
        }
    }

}
