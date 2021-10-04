package Engine.Graphics.Renderers;

import Engine.Graphics.Shader;
import Engine.Objects.GameObject;

public final class UnlitRenderer extends Renderer {


    @Override
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
    }

    @Override
    public void SetUniforms(GameObject gameObject) {

    }

}
