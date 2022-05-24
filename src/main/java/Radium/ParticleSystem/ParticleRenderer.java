package Radium.ParticleSystem;

import Radium.Components.Particles.ParticleSystem;
import Radium.Graphics.Shader.Shader;
import Radium.Math.Vector.Vector3;
import Radium.Variables;
import RadiumEditor.Console;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public class ParticleRenderer {

    private transient ParticleBatch batch;
    private transient Shader shader;

    private transient ParticleSystem system;

    public ParticleRenderer(ParticleBatch batch, ParticleSystem system) {
        this.batch = batch;
        this.shader = new Shader("EngineAssets/Shaders/Particle/vert.glsl", "EngineAssets/Shaders/Particle/frag.glsl");
        this.system = system;
    }

    public void Render() {
        int blendFunc = GL11.GL_ONE_MINUS_SRC_ALPHA;
        if (system.blendType == BlendType.Additive) {
            blendFunc = GL11.GL_SRC_ALPHA;
        }
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, blendFunc);

        GL30.glBindVertexArray(batch.mesh.GetVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, batch.mesh.GetIBO());
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, batch.texture.textureID);

        shader.Bind();
        for (Particle particle : batch.particles) {
            Matrix4f view = Variables.DefaultCamera.GetView();
            Matrix4f transform = particle.CalculateTransform(view);
            Matrix4f projection = Variables.DefaultCamera.GetProjection();
            Matrix4f modelView = new Matrix4f(view).mul(transform);

            shader.SetUniform("modelView", modelView);
            shader.SetUniform("model", transform);
            shader.SetUniform("view", view);
            shader.SetUniform("projection", projection);
            shader.SetUniform("color", particle.color.ToVector3());
            shader.SetUniform("alphaIsTransparency", system.blendType == BlendType.Additive);

            GL11.glDrawElements(GL11.GL_TRIANGLES, batch.mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);
        }
        shader.Unbind();

        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

}
