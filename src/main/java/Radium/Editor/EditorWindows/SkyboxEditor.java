package Radium.Editor.EditorWindows;

import Radium.Engine.Graphics.Texture;
import Radium.Engine.Skybox;
import Radium.Editor.EditorGUI;
import Radium.Editor.EditorWindow;

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

    
    public void RenderGUI() {
        Texture top = EditorGUI.TextureField(Skybox.individualTextures[2]);
        Texture bottom = EditorGUI.TextureField(Skybox.individualTextures[3]);
        Texture front = EditorGUI.TextureField(Skybox.individualTextures[5]);
        Texture back = EditorGUI.TextureField(Skybox.individualTextures[4]);
        Texture left = EditorGUI.TextureField(Skybox.individualTextures[0]);
        Texture right = EditorGUI.TextureField(Skybox.individualTextures[1]);

        if (top != null) {
            Skybox.textures[2] = top.filepath;
            Skybox.UpdateTextures();
        } else if (bottom != null) {
            Skybox.textures[3] = bottom.filepath;
            Skybox.UpdateTextures();
        } else if (front != null) {
            Skybox.textures[5] = front.filepath;
            Skybox.UpdateTextures();
        } else if (back != null) {
            Skybox.textures[4] = back.filepath;
            Skybox.UpdateTextures();
        } else if (left != null) {
            Skybox.textures[0] = left.filepath;
            Skybox.UpdateTextures();
        } else if (right != null) {
            Skybox.textures[1] = right.filepath;
            Skybox.UpdateTextures();
        }
    }

}
