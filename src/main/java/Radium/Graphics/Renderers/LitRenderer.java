package Radium.Graphics.Renderers;

import Radium.Components.Graphics.MeshFilter;
import Radium.Graphics.Shader;
import Radium.Objects.GameObject;
import Radium.Variables;

public final class LitRenderer extends Renderer {

    @Override
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Lit/vert.glsl", "EngineAssets/Shaders/Lit/frag.glsl");
    }

    @Override
    public void SetUniforms(GameObject gameObject) {
        gameObject.GetComponent(MeshFilter.class).SentMaterialToShader(shader);
        shader.SetUniform("cameraPosition", Variables.DefaultCamera.gameObject.transform.position);
    }

}
