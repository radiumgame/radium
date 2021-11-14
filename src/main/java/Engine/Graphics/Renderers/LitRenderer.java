package Engine.Graphics.Renderers;

import Engine.Components.Graphics.MeshFilter;
import Engine.Graphics.Shader;
import Engine.Objects.GameObject;

public final class LitRenderer extends Renderer {

    @Override
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Lit/vert.glsl", "EngineAssets/Shaders/Lit/frag.glsl");
    }

    @Override
    public void SetUniforms(GameObject gameObject) {
        gameObject.GetComponent(MeshFilter.class).SentMaterialToShader(shader);
    }

}
