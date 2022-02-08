package Radium.UI;

import Radium.Components.UI.Image;
import Radium.Graphics.Shader;
import Radium.Math.Mathf;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 * Renders {@link UIMesh meshes}
 */
public class UIRenderer {

    private static Shader shader;
    private static Matrix4f projection;

    private static Vector2 canvasSize = new Vector2(1920, 1080);

    protected UIRenderer() {}

    /**
     * Initialize the UI shader
     */
    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/UI/vert.glsl", "EngineAssets/Shaders/UI/frag.glsl");

        projection = new Matrix4f();
        projection.ortho(0, canvasSize.x, canvasSize.y, 0, -1f, 1f);
    }

    /**
     * Render a UI mesh
     * @param mesh Mesh to render
     */
    public static void Render(UIMesh mesh) {
        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, mesh.texture.textureID);

        shader.Bind();

        shader.SetUniform("model", Model(mesh));
        shader.SetUniform("projection", projection);

        shader.SetUniform("color", mesh.color.ToVector3());
        shader.SetUniform("alpha", mesh.color.a);

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.indices.length, GL11.GL_UNSIGNED_INT, 0);

        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    private static Matrix4f Model(UIMesh mesh) {
        Matrix4f transformMatrix = new Matrix4f().identity();
        transformMatrix.translate(mesh.Position.x, mesh.Position.y, 0);
        transformMatrix.scale(mesh.Size.x, mesh.Size.y, 1);

        return transformMatrix;
    }

}
