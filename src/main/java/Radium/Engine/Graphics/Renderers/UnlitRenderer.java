package Radium.Engine.Graphics.Renderers;

import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Objects.GameObject;

public final class UnlitRenderer extends Renderer {

    
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
    }

    
    public void SetUniforms(GameObject gameObject) {
        shader.SetUniform("color", gameObject.GetComponent(MeshFilter.class).material.color.ToVector3());
    }

}
