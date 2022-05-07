package Radium.Graphics.Renderers;

import Radium.Components.Graphics.MeshFilter;
import Radium.Graphics.Shader.Shader;
import Radium.Objects.GameObject;

public final class UnlitRenderer extends Renderer {

    
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
    }

    
    public void SetUniforms(GameObject gameObject) {
        shader.SetUniform("color", gameObject.GetComponent(MeshFilter.class).material.color.ToVector3());
    }

}
