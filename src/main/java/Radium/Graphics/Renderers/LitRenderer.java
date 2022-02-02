package Radium.Graphics.Renderers;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.Outline;
import Radium.Graphics.Shader;
import Radium.Input.Input;
import Radium.Math.Vector.Vector2;
import Radium.Objects.GameObject;
import Radium.Window;
import RadiumEditor.Console;
import RadiumEditor.Viewport;

public final class LitRenderer extends Renderer {

    @Override
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Lit/vert.glsl", "EngineAssets/Shaders/Lit/frag.glsl");
    }

    @Override
    public void SetUniforms(GameObject gameObject) {
        gameObject.GetComponent(MeshFilter.class).SendMaterialToShader(shader);

        Outline outline = gameObject.GetComponent(Outline.class);
        if (outline != null) {
            outline.SendUniforms();
        }
    }

}
