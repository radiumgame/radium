package Radium.Editor.Debug;

import Radium.Engine.Color.Color;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Graphics.Vertex;
import Radium.Engine.Math.Mathf;
import Radium.Engine.Math.Matrix4;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Variables;
import Radium.Engine.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

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
     * Initializes mesh and render batch
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

    public static void Reset() {
        mesh.Destroy();
        shader.Destroy();

        Initialize();
    }

    /**
     * Renders gridlines
     */
    public static void Render() {
        GL11.glDepthMask(false);
        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        Matrix4f view = Matrix4.View(Variables.EditorCamera.transform);

        shader.Bind();
        shader.SetUniform("view", view);
        shader.SetUniform("projection", projection);
        shader.SetUniform("gridScale", GridScale);
        shader.SetUniform("gridColor", GridColor.ToVector3());
        shader.SetUniform("xAxisColor", XAxisColor.ToVector3());
        shader.SetUniform("zAxisColor", ZAxisColor.ToVector3());
        shader.SetUniform("cameraPosition", Variables.EditorCamera.transform.position);

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);
        shader.Unbind();

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL11.glDepthMask(true);
    }
}
