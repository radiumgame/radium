package Radium.Graphics.Renderers;

import Radium.Graphics.Shader;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;

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
