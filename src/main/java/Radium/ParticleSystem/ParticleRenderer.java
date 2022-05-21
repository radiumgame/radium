package Radium.ParticleSystem;

import Radium.Graphics.Shader.Shader;
import Radium.Math.Vector.Vector3;
import Radium.Variables;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class ParticleRenderer {

    private ParticleBatch batch;
    private Shader shader;

    public ParticleRenderer(ParticleBatch batch) {
        this.batch = batch;
        this.shader = new Shader("EngineAssets/Shaders/Particle/vert.glsl", "EngineAssets/Shaders/Particle/frag.glsl");
    }

    public void Render() {
        GL30.glBindVertexArray(batch.mesh.GetVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, batch.mesh.GetIBO());
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, batch.texture.textureID);

        shader.Bind();
        for (Particle particle : batch.particles) {
            shader.SetUniform("model", particle.CalculateTransform());
            shader.SetUniform("view", Variables.DefaultCamera.GetView());
            shader.SetUniform("projection", Variables.DefaultCamera.GetProjection());
            shader.SetUniform("color", new Vector3(1, 1, 1));

            GL11.glDrawElements(GL11.GL_TRIANGLES, batch.mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);
        }
        shader.Unbind();

        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

}
