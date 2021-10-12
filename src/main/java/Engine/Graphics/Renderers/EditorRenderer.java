package Engine.Graphics.Renderers;

import Engine.Components.Graphics.MeshFilter;
import Engine.Graphics.Mesh;
import Engine.Graphics.Shader;
import Engine.Math.Matrix4;
import Engine.Objects.EditorObject;
import Engine.Objects.GameObject;
import Engine.Util.NonInstantiatable;
import Engine.Variables;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public final class EditorRenderer extends NonInstantiatable {

    private static Shader shader;

    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
    }

    public static void Render(EditorObject editorObject) {
        Mesh mesh = editorObject.mesh;
        if (mesh == null) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, mesh.GetMaterial().GetTextureID());

        shader.Bind();

        shader.SetUniform("model", Matrix4.Transform(editorObject.transform));
        shader.SetUniform("view", Matrix4.View(Variables.EditorCamera.transform));
        shader.SetUniform("projection", Variables.EditorCamera.projection);

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

        GL11.glDisable(GL11.GL_BLEND);
    }

}
