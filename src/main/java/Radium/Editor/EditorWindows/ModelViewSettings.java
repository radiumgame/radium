package Radium.Editor.EditorWindows;

import Radium.Engine.Graphics.Texture;
import Radium.Editor.EditorGUI;
import Radium.Editor.EditorWindow;

public class ModelViewSettings extends EditorWindow {

    public static Texture base = new Texture("EngineAssets/Textures/Misc/blank.jpg", false);

    public ModelViewSettings() {
        MenuName = "Model Viewer Settings";
    }

    @Override
    public void Start() {

    }

    @Override
    public void RenderGUI() {
        Texture t = EditorGUI.TextureField(base);
        if (t != null) {
            base = t;
        }
    }
}
