package RadiumEditor.Debug;

import Radium.Color;
import Radium.Graphics.BatchRendering.BatchRenderer;
import Radium.Graphics.BatchRendering.RenderBatch;
import Radium.Graphics.Mesh;
import Radium.Graphics.Shader.Shader;
import Radium.Graphics.Vertex;
import Radium.Math.Mathf;
import Radium.Math.Matrix4;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Variables;
import Radium.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;

/**
 * Basic editor gridlines
 */
public class GridLines {

    private static Shader shader;
    private static Matrix4f projection;

    public static float GridScale = 0.5f;
    public static Color GridColor = new Color(0.8f, 0.8f, 0.8f, 1.0f);
    public static Color XAxisColor = Color.Red();
    public static Color ZAxisColor = Color.Blue();

    private static Mesh mesh;

    protected GridLines() {}

    /**
     * Intializes mesh and render batch
     */
    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Grid/vert.glsl", "EngineAssets/Shaders/Grid/frag.glsl");
        projection = new Matrix4f().perspective(Mathf.Radians(70f), (float)Window.width / (float)Window.height, 0.1f, Variables.EditorCamera.far);

        Vertex[] vertices =  {
            new Vertex(new Vector3(1, 1, 0), Vector2.Zero()),
            new Vertex(new Vector3(-1, -1, 0), Vector2.Zero()),
            new Vertex(new Vector3(-1, 1, 0), Vector2.Zero()),
            new Vertex(new Vector3(1, -1, 0), Vector2.Zero()),
        };
        int[] indices = {
            0, 3, 1, 1, 2, 0
        };
        mesh = new Mesh(vertices, indices);
    }

    /**
     * Renders gridlines
     */
    public static void Render() {
        GL11.glDepthMask(false);
        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        Matrix4f view = Matrix4.View(Variables.EditorCamera.transform);

        shader.Bind();
        shader.SetUniform("view", view);
        shader.SetUniform("projection", projection);
        shader.SetUniform("gridScale", GridScale);
        shader.SetUniform("gridColor", GridColor.ToVector3());
        shader.SetUniform("xAxisColor", XAxisColor.ToVector3());
        shader.SetUniform("zAxisColor", ZAxisColor.ToVector3());

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);
        shader.Unbind();

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL11.glDepthMask(true);
    }
}
