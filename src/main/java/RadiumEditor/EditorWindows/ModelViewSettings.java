package RadiumEditor.EditorWindows;

import Radium.Graphics.Texture;
import RadiumEditor.EditorGUI;
import RadiumEditor.EditorWindow;

public class ModelViewSettings extends EditorWindow {

    public static Texture base = new Texture("EngineAssets/Textures/Misc/blank.jpg");

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
