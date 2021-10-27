package Engine.ParticleSystem;

import Engine.Application;
import Engine.Graphics.BatchRendering.RenderBatch;
import Engine.Graphics.Mesh;
import Engine.Graphics.Shader;
import Engine.Math.Matrix4;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector3;
import Engine.Variables;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class ParticleRenderer {

    public ParticleBatch batch;
    private Shader shader;

    public ParticleRenderer(ParticleBatch batch) {
        this.batch = batch;

        shader = new Shader("EngineAssets/Shaders/Particle/vert.glsl", "EngineAssets/Shaders/Particle/frag.glsl");
    }

    public void Render() {
        if (batch.mesh == null || Variables.DefaultCamera == null) return;
        Mesh mesh = batch.mesh;

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, mesh.GetMaterial().GetTextureID());

        Matrix4f view = Matrix4.View(Variables.DefaultCamera.gameObject.transform);

        shader.Bind();
        for (int i = 0; i < batch.particles.size(); i++) {
            Particle particle = batch.particles.get(i);

            shader.SetUniform("model", Matrix4.Transform(particle.transform));
            shader.SetUniform("view", view);
            shader.SetUniform("projection", Variables.DefaultCamera.GetProjection());
            shader.SetUniform("color", particle.color.ToVector3());

            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);
            particle.Update();
        }
        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

}
