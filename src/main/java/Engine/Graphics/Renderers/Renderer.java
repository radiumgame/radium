package Engine.Graphics.Renderers;

import Engine.Application;
import Engine.Components.Graphics.MeshFilter;
import Engine.Graphics.Framebuffer.DepthFramebuffer;
import Engine.Graphics.Mesh;
import Engine.Graphics.Shader;
import Engine.Graphics.Shadows.Shadows;
import Engine.Math.Matrix4;
import Engine.Objects.GameObject;
import Engine.Skybox;
import Engine.Variables;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public abstract class Renderer {

    public Shader shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");

    public Renderer() {
        Initialize();
    }

    public abstract void Initialize();
    public abstract void SetUniforms(GameObject gameObject);

    public void Render(GameObject gameObject) {
        if (!gameObject.ContainsComponent(MeshFilter.class)) return;

        Mesh mesh = gameObject.GetComponent(MeshFilter.class).mesh;
        if (mesh == null) return;

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, mesh.GetMaterial().GetTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, Shadows.framebuffer.GetDepthMap());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL13.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, Skybox.GetTexture());

        shader.Bind();

        shader.SetUniform("depthTestFrame", DepthFramebuffer.DepthTesting);

        shader.SetUniform("model", Matrix4.Transform(gameObject.transform));
        shader.SetUniform("view", Application.Playing ? Variables.DefaultCamera.GetView() : Variables.EditorCamera.GetView());
        shader.SetUniform("projection", Application.Playing ? Variables.DefaultCamera.GetProjection() : Variables.EditorCamera.GetProjection());

        shader.SetUniform("tex", 0);
        shader.SetUniform("lightDepth", 1);
        shader.SetUniform("environmentMap", 2);

        SetUniforms(gameObject);

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL13.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glDisableVertexAttribArray(3);

        GL30.glBindVertexArray(0);
    }

}
