package Engine.Graphics;

import Engine.Components.Camera;
import Engine.Math.Matrix4;
import Engine.Objects.GameObject;
import Engine.Variables;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public final class Renderer {

    public Renderer() {
        throw new UnsupportedOperationException("Cannot instantiate Renderer class");
    }

    private static Shader shader;

    public static void Init() {
        shader = new Shader("EngineAssets/Shaders/vert.glsl", "EngineAssets/Shaders/frag.glsl");
    }

    public static void Render(GameObject gameObject, Mesh mesh, Camera camera) {
        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, mesh.GetMaterial().GetTextureID());

        shader.Bind();

        shader.SetUniform("model", Matrix4.Transform(gameObject.transform));
        shader.SetUniform("view", Matrix4.View(camera.gameObject.transform));
        shader.SetUniform("projection", Variables.DefaultCamera.GetProjection());

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

}
