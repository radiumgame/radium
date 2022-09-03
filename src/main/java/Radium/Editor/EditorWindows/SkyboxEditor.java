package Radium.Editor.EditorWindows;

import Radium.Engine.Skybox.CubemapSkybox;
import Radium.Engine.Skybox.Skybox;
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
        File top = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[2]);
        File bottom = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[3]);
        File front = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[5]);
        File back = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[4]);
        File left = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[0]);
        File right = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[1]);

        if (top != null) {
            CubemapSkybox.textures[2] = top.getPath();
            CubemapSkybox.UpdateTextures();
        } else if (bottom != null) {
            CubemapSkybox.textures[3] = bottom.getPath();
            CubemapSkybox.UpdateTextures();
        } else if (front != null) {
            CubemapSkybox.textures[5] = front.getPath();
            CubemapSkybox.UpdateTextures();
        } else if (back != null) {
            CubemapSkybox.textures[4] = back.getPath();
            CubemapSkybox.UpdateTextures();
        } else if (left != null) {
            CubemapSkybox.textures[0] = left.getPath();
            CubemapSkybox.UpdateTextures();
        } else if (right != null) {
            CubemapSkybox.textures[1] = right.getPath();
            CubemapSkybox.UpdateTextures();
        }
    }

}
