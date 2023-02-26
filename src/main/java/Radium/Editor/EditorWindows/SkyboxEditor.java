package Radium.Editor.EditorWindows;

import Radium.Editor.Files.Parser;
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
        File top = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[2], "SkyboxTexture1", Parser.images, Parser.loadedImages);
        File bottom = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[3], "SkyboxTexture2", Parser.images, Parser.loadedImages);
        File front = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[5], "SkyboxTexture3", Parser.images, Parser.loadedImages);
        File back = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[4], "SkyboxTexture4", Parser.images, Parser.loadedImages);
        File left = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[0], "SkyboxTexture5", Parser.images, Parser.loadedImages);
        File right = EditorGUI.FileReceive(allowedTypes, "Image", CubemapSkybox.individualTextures[1], "SkyboxTexture6", Parser.images, Parser.loadedImages);

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
