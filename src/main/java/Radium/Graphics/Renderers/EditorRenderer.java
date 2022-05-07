package Radium.Graphics.Renderers;

import RadiumEditor.Console;
import Radium.Graphics.Mesh;
import Radium.Graphics.Shader.Shader;
import Radium.Math.Vector.Vector3;
import Radium.Objects.EditorObject;
import Radium.Variables;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class EditorRenderer {

    private static Shader shader;

    protected EditorRenderer() {}

    /**
     * Initialize the editor renderers shader
     */
    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
    }

    /**
     * Renders editor object
     * @param editorObject Editor object to render
     * @param model Transform matrix for renderer to use
     * @param view View matrix for renderer to use
     */
    public static void Render(EditorObject editorObject, Matrix4f model, Matrix4f view) {
        Mesh mesh = editorObject.mesh;
        if (mesh == null) return;

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, editorObject.material.GetTextureID());

        shader.Bind();

        shader.SetUniform("model", model);
        shader.SetUniform("view", view);
        shader.SetUniform("projection", Variables.EditorCamera.GetProjection());
        shader.SetUniform("color", Vector3.One());

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

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
