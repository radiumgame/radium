package Engine.Graphics.Renderers;

import Engine.Graphics.Shader;
import Engine.Math.Vector.Vector3;
import Engine.Objects.GameObject;

public final class UnlitRenderer extends Renderer {


    @Override
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
    }

    @Override
    public void SetUniforms(GameObject gameObject) {
        shader.SetUniform("color", Vector3.One);
    }

}
