package Radium.Graphics.Renderers;

import Radium.Application;
import Radium.Components.Graphics.MeshFilter;
import Radium.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Graphics.Shader.ShaderUniform;
import Radium.Graphics.Shadows.Shadows;
import Radium.Graphics.Texture;
import Radium.Math.Matrix4;
import Radium.Math.Vector.Vector2;
import Radium.Objects.GameObject;
import Radium.Time;
import Radium.Variables;
import RadiumEditor.Console;
import RadiumRuntime.Runtime;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class CustomRenderer extends Renderer {

    public void Initialize() {

    }

    public void SetUniforms(GameObject gameObject) {
        if (shader == null) return;

        shader.SetUniform("gameObject.position", gameObject.transform.WorldPosition());
        shader.SetUniform("gameObject.rotation", gameObject.transform.WorldRotation());
        shader.SetUniform("gameObject.scale", gameObject.transform.WorldScale());
        shader.SetUniform("gameObject.localPosition", gameObject.transform.localPosition);
        shader.SetUniform("gameObject.localRotation", gameObject.transform.localRotation);
        shader.SetUniform("gameObject.localScale", gameObject.transform.localScale);

        shader.SetUniform("cameraPosition", Application.Playing ? Variables.DefaultCamera.gameObject.transform.WorldPosition() : Variables.EditorCamera.transform.position);

        shader.SetUniform("time", Time.GetTime());
        shader.SetUniform("deltaTime", Time.deltaTime);
        shader.SetUniform("viewDirection", (Application.Playing) ? Variables.DefaultCamera.gameObject.transform.Forward() : Variables.EditorCamera.transform.EditorForward());
        shader.SetUniform("resolution", new Vector2(1920, 1080));
    }

    public void Render(GameObject gameObject) {
        if (!gameObject.ContainsComponent(MeshFilter.class)) return;
        if (Variables.DefaultCamera == null && Application.Playing) return;

        MeshFilter meshFilter = gameObject.GetComponent(MeshFilter.class);
        if (meshFilter.mesh == null) return;

        GL30.glBindVertexArray(meshFilter.mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);
        GL30.glEnableVertexAttribArray(4);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshFilter.mesh.GetIBO());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Runtime.renderFramebuffer.GetTextureID());
        int texIndex = 1;
        for (int i = 0; i < shader.uniforms.size(); i++) {
            ShaderUniform uniform = shader.uniforms.get(i);
            if (uniform.type == Texture.class && uniform.value != null) {
                uniform.UpdateType();
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + texIndex);
                GL13.glBindTexture(GL11.GL_TEXTURE_2D, ((Texture)uniform.value).textureID);
                uniform.temp = texIndex;
                texIndex++;
            }
        }

        shader.Bind();

        shader.SetUniform("model", Matrix4.Transform(gameObject.transform));
        shader.SetUniform("view", Application.Playing ? Variables.DefaultCamera.GetView() : Variables.EditorCamera.GetView());
        shader.SetUniform("projection", Application.Playing ? Variables.DefaultCamera.GetProjection() : Variables.EditorCamera.GetProjection());

        shader.SetUniform("screen", 0);
        SetUniforms(gameObject);
        for (ShaderUniform uniform : shader.GetUniforms()) {
            if (uniform.type == Texture.class) {
                if (uniform.value != null) {
                    shader.SetUniform(uniform.name, uniform.temp);
                    uniform.temp = 0;
                }
            } else {
                uniform.Set();
            }
        }

        GL11.glDrawElements(GL11.GL_TRIANGLES, meshFilter.mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL13.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glDisableVertexAttribArray(3);
        GL30.glDisableVertexAttribArray(4);

        GL30.glBindVertexArray(0);
    }

}
