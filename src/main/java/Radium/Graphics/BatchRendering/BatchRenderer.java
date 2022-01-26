package Radium.Graphics.BatchRendering;

import Radium.Graphics.Mesh;
import Radium.Graphics.Shader;
import Radium.Math.Matrix4;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector3;
import Radium.Variables;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class BatchRenderer {

    public RenderBatch batch;
    private Shader shader;

    private Matrix4f projection;
    private boolean customProjection = false;

    public BatchRenderer(RenderBatch batch) {
        this.batch = batch;

        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
        customProjection = false;
    }

    public BatchRenderer(RenderBatch batch, Matrix4f customProjection) {
        this.batch = batch;

        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
        projection = customProjection;
        this.customProjection = true;
    }

    public void Render() {
        if (batch.mesh == null) return;
        Mesh mesh = batch.mesh;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, batch.material.GetTextureID());

        Matrix4f view = Matrix4.View(Variables.EditorCamera.transform);

        shader.Bind();
        for (Transform transform : batch.batchObjectTransforms) {
            shader.SetUniform("model", Matrix4.Transform(transform));
            shader.SetUniform("view", view);
            shader.SetUniform("projection", customProjection ? projection : Variables.EditorCamera.GetProjection());
            shader.SetUniform("color", Vector3.One);

            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);
        }
        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glDisableVertexAttribArray(3);

        GL30.glBindVertexArray(0);

        GL11.glDisable(GL11.GL_BLEND);
    }

}
