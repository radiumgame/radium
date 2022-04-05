package Radium.Graphics.Renderers;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.Outline;
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
        shader.SetUniform("color", gameObject.GetComponent(MeshFilter.class).material.color.ToVector3());

        Outline outline = gameObject.GetComponent(Outline.class);
        if (outline != null) {
            outline.SendUniforms();
        }
    }

}
