package Radium.Engine.Graphics.Renderers;

import Radium.Engine.Application;
import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Variables;

public final class LitRenderer extends Renderer {

    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Lit/vert.glsl", "EngineAssets/Shaders/Lit/frag.glsl", "EngineAssets/Shaders/Lit/geom.glsl");
    }
    
    public void SetUniforms(GameObject gameObject) {
        gameObject.GetComponent(MeshFilter.class).SendMaterialToShader(shader);

        Vector3 cameraPosition = Application.Playing ? Variables.DefaultCamera.gameObject.transform.WorldPosition() : Variables.EditorCamera.transform.position;
        shader.SetUniform("cameraPosition", cameraPosition);
    }

}
