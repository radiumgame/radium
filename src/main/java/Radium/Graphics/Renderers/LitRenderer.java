package Radium.Graphics.Renderers;

import Radium.Components.Graphics.MeshFilter;
import Radium.Graphics.Shader.Shader;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.Time;
import Radium.Variables;

public final class LitRenderer extends Renderer {

    
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Lit/vert.glsl", "EngineAssets/Shaders/Lit/frag.glsl");
    }
    
    public void SetUniforms(GameObject gameObject) {
        gameObject.GetComponent(MeshFilter.class).SendMaterialToShader(shader);

        Vector3 cameraPosition = Variables.DefaultCamera.gameObject.transform.WorldPosition();
        shader.SetUniform("cameraPosition", cameraPosition);
    }

}
