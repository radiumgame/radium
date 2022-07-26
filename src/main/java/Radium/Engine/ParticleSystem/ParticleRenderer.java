package Radium.Engine.ParticleSystem;

import Radium.Editor.Console;
import Radium.Engine.Components.Particles.ParticleSystem;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Variables;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public class ParticleRenderer {

    private transient final ParticleBatch batch;
    private transient final Shader shader;

    private transient int pointer = 0;

    private transient final ParticleSystem system;

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

        GL30.glBindVertexArray(batch.mesh);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);
        GL30.glEnableVertexAttribArray(4);
        GL30.glEnableVertexAttribArray(5);
        GL30.glEnableVertexAttribArray(6);
        GL30.glEnableVertexAttribArray(7);
        GL30.glEnableVertexAttribArray(8);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, batch.meshIbo);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, batch.texture.textureID);

        Matrix4f projection = Variables.DefaultCamera.GetProjection();

        shader.Bind();
        shader.SetUniform("projection", projection);
        shader.SetUniform("numberOfRows", system.atlasSize.x);
        shader.SetUniform("alphaIsTransparency", system.blendType == BlendType.Additive);

        system.CheckVBOData();

        float[] vboData = new float[batch.particles.size() * batch.DataLength];
        for (Particle particle : batch.particles) {
            Matrix4f view = Variables.DefaultCamera.GetView();
            Matrix4f transform = particle.CalculateTransform(view);
            Matrix4f modelView = new Matrix4f(view).mul(transform);

            StoreMatrix(modelView, vboData);
            UpdateParticleData(particle, vboData);
        }
        batch.UpdateVBO(vboData);
        pointer = 0;

        GL33.glDrawElementsInstanced(GL11.GL_TRIANGLES, batch.indicesCount, GL11.GL_UNSIGNED_INT, 0, batch.particles.size());
        shader.Unbind();

        GL30.glDisableVertexAttribArray(8);
        GL30.glDisableVertexAttribArray(7);
        GL30.glDisableVertexAttribArray(6);
        GL30.glDisableVertexAttribArray(5);
        GL30.glDisableVertexAttribArray(4);
        GL30.glDisableVertexAttribArray(3);
        GL30.glDisableVertexAttribArray(2);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void StoreMatrix(Matrix4f matrix, float[] vboData) {
        vboData[pointer++] = matrix.m00();
        vboData[pointer++] = matrix.m01();
        vboData[pointer++] = matrix.m02();
        vboData[pointer++] = matrix.m03();
        vboData[pointer++] = matrix.m10();
        vboData[pointer++] = matrix.m11();
        vboData[pointer++] = matrix.m12();
        vboData[pointer++] = matrix.m13();
        vboData[pointer++] = matrix.m20();
        vboData[pointer++] = matrix.m21();
        vboData[pointer++] = matrix.m22();
        vboData[pointer++] = matrix.m23();
        vboData[pointer++] = matrix.m30();
        vboData[pointer++] = matrix.m31();
        vboData[pointer++] = matrix.m32();
        vboData[pointer++] = matrix.m33();
    }

    private void UpdateParticleData(Particle particle, float[] vboData) {
        vboData[pointer++] = particle.textureOffset1.x;
        vboData[pointer++] = particle.textureOffset1.y;
        vboData[pointer++] = particle.textureOffset2.x;
        vboData[pointer++] = particle.textureOffset2.y;
        vboData[pointer++] = particle.color.r;
        vboData[pointer++] = particle.color.g;
        vboData[pointer++] = particle.color.b;
        vboData[pointer++] = particle.blend;
    }

}
