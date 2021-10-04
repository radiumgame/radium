package Engine.Graphics.Renderers;

import Engine.Components.Graphics.MeshFilter;
import Engine.Graphics.Shader;
import Engine.Math.Vector.Vector3;
import Engine.Objects.GameObject;
import Engine.Variables;

public final class LitRenderer extends Renderer {

    @Override
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Lit/vert.glsl", "EngineAssets/Shaders/Lit/frag.glsl");
    }

    @Override
    public void SetUniforms(GameObject gameObject) {
        gameObject.GetComponent(MeshFilter.class).SentMaterialToShader(shader);
        shader.SetUniform("ambient", 0.5f);
    }

}
