package Radium.Graphics.Renderers;

import Radium.Application;
import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Rendering.Light;
import Radium.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Graphics.Shader.Shader;
import Radium.Math.Matrix4;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.SceneManagement.SceneManager;
import Radium.Variables;
import RadiumEditor.Console;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public final class MousePickingRenderer {

    private static Shader shader;

    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/MousePicking/vert.glsl", "EngineAssets/Shaders/MousePicking/frag.glsl");
    }
    
    public static void Render(GameObject gameObject) {
        MeshFilter meshFilter = gameObject.GetComponent(MeshFilter.class);

        GL30.glBindVertexArray(meshFilter.mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshFilter.mesh.GetIBO());

        shader.Bind();

        shader.SetUniform("model", Matrix4.Transform(gameObject.transform));
        shader.SetUniform("view", Variables.EditorCamera.GetView());
        shader.SetUniform("projection", Variables.EditorCamera.GetProjection());

        int id = SceneManager.GetCurrentScene().gameObjectsInScene.indexOf(gameObject) + 1;
        int r = (id & 0x000000FF);
        int g = (id & 0x0000FF00) >>  8;
        int b = (id & 0x00FF0000) >> 16;
        shader.SetUniform("id", new Vector3(r / 255f, g / 255f, b / 255f));

        GL11.glDrawElements(GL11.GL_TRIANGLES, meshFilter.mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

}
