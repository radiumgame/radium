package Radium.ParticleSystem;

import Radium.Graphics.Mesh;
import Radium.Graphics.Shader.Shader;
import Radium.Math.Mathf;
import Radium.Math.Matrix4;
import Radium.Variables;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 * Efficient particle renderer using batch rendering
 */
public class ParticleRenderer {

    /**
     * Particle batch to render
     */
    public ParticleBatch batch;
    private Shader shader;

    /**
     * Create particle renderer from batch
     * @param batch Particle batch
     */
    public ParticleRenderer(ParticleBatch batch) {
        this.batch = batch;

        shader = new Shader("EngineAssets/Shaders/Particle/vert.glsl", "EngineAssets/Shaders/Particle/frag.glsl");
    }

    /**
     * Render every particle
     */
    public void Render() {
        if (batch.mesh == null || Variables.DefaultCamera == null) return;
        Mesh mesh = batch.mesh;

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, batch.material.GetTextureID());

        Matrix4f view = Matrix4.View(Variables.DefaultCamera.gameObject.transform);

        shader.Bind();
        for (int i = 0; i < batch.particles.size(); i++) {
            Particle particle = batch.particles.get(i);

            Matrix4f transformMatrix = new Matrix4f().identity();
            transformMatrix.translate(particle.transform.position.x, particle.transform.position.y, particle.transform.position.z);
            transformMatrix.rotateX(Mathf.Radians(particle.transform.rotation.x));
            transformMatrix.rotateY(Mathf.Radians(particle.transform.rotation.y));
            transformMatrix.rotateZ(Mathf.Radians(particle.transform.rotation.z));
            transformMatrix.scale(particle.transform.scale.x, particle.transform.scale.y, particle.transform.scale.z);

            shader.SetUniform("model", transformMatrix);
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
