package Radium.Graphics.Renderers;

import Radium.Application;
import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.Outline;
import Radium.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Graphics.Shader.Shader;
import Radium.Graphics.Shadows.Shadows;
import Radium.Math.Matrix4;
import Radium.Objects.GameObject;
import Radium.Variables;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public final class LitRenderer extends Renderer {

    
    public void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Lit/vert.glsl", "EngineAssets/Shaders/Lit/frag.glsl");
    }
    
    public void SetUniforms(GameObject gameObject) {
        gameObject.GetComponent(MeshFilter.class).SendMaterialToShader(shader);

        Outline outline = gameObject.GetComponent(Outline.class);
        if (outline != null) {
            outline.SendUniforms();
        }
    }

}
