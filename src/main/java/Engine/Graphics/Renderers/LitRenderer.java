package Engine.Graphics.Renderers;

import Engine.Graphics.Shader;
import Engine.Math.Vector.Vector3;
import Engine.Variables;

public final class LitRenderer extends Renderer {

    @Override
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Lit/vert.glsl", "EngineAssets/Shaders/Lit/frag.glsl");
    }

    @Override
    public void SetUniforms() {
        shader.SetUniform("ambient", 0.5f);
    }

}
