package Radium.Graphics.BatchRendering;

import Radium.Graphics.Mesh;
import Radium.Graphics.Shader.Shader;
import Radium.Math.Matrix4;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector3;
import Radium.Variables;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 * A renderer that batches objects for efficient rendering
 */
public class BatchRenderer {

    /**
     * The batch to render
     */
    public RenderBatch batch;
    private Shader shader;

    private Matrix4f projection;
    private boolean customProjection = false;

    /**
     * Creates a batch renderer
     * @param batch The batch to render
     */
    public BatchRenderer(RenderBatch batch) {
        this.batch = batch;

        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
        customProjection = false;
    }

    /**
     * Creates a batch with a custom projection matrix
     * @param batch Batch to render
     * @param customProjection Projection to use
     */
    public BatchRenderer(RenderBatch batch, Matrix4f customProjection) {
        this.batch = batch;

        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
        projection = customProjection;
        this.customProjection = true;
    }

    /**
     * Render the current batch
     */
    public void Render() {
        if (batch.mesh == null) return;
        Mesh mesh = batch.mesh;

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
            shader.SetUniform("model", Matrix4.Transform(transform, false));
            shader.SetUniform("view", view);
            shader.SetUniform("projection", customProjection ? projection : Variables.EditorCamera.GetProjection());
            shader.SetUniform("color", Vector3.One());

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
    }

}
