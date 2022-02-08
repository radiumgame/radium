package Radium.Graphics.Renderers;

import Radium.Application;
import Radium.Components.Graphics.MeshFilter;
import Radium.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Graphics.Mesh;
import Radium.Graphics.Shader;
import Radium.Graphics.Shadows.Shadows;
import Radium.Math.Matrix4;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.SceneManagement.SceneManager;
import Radium.Skybox;
import Radium.Variables;
import RadiumEditor.Console;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

/**
 * A base rendering class with basic rendering implementation
 */
public abstract class Renderer {

    public Shader shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");

    public Renderer() {
        Initialize();
    }

    /**
     * Initializes the renderers shader
     */
    public abstract void Initialize();

    /**
     * Set the shader uniforms
     * @param gameObject Rendering game object
     */
    public abstract void SetUniforms(GameObject gameObject);

    /**
     * Render game object
     * @param gameObject Gameobject to render
     */
    public void Render(GameObject gameObject) {
        if (!gameObject.ContainsComponent(MeshFilter.class)) return;
        if (Variables.DefaultCamera == null && Application.Playing) return;

        MeshFilter meshFilter = gameObject.GetComponent(MeshFilter.class);
        if (meshFilter.mesh == null) return;

        GL30.glBindVertexArray(meshFilter.mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);
        GL30.glEnableVertexAttribArray(4);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshFilter.mesh.GetIBO());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, meshFilter.material.GetTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, meshFilter.material.GetNormalTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, meshFilter.material.GetSpecularMapID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, Shadows.framebuffer.GetDepthMap());

        shader.Bind();

        shader.SetUniform("depthTestFrame", DepthFramebuffer.DepthTesting);

        shader.SetUniform("color", meshFilter.material.color.ToVector3());

        shader.SetUniform("model", Matrix4.Transform(gameObject.transform));
        shader.SetUniform("view", Application.Playing ? Variables.DefaultCamera.GetView() : Variables.EditorCamera.GetView());
        shader.SetUniform("projection", Application.Playing ? Variables.DefaultCamera.GetProjection() : Variables.EditorCamera.GetProjection());

        shader.SetUniform("tex", 0);
        shader.SetUniform("normalMap", 1);
        shader.SetUniform("specularMap", 2);
        shader.SetUniform("lightDepth", 3);

        shader.SetUniform("objectID", SceneManager.GetCurrentScene().gameObjectsInScene.indexOf(gameObject));

        SetUniforms(gameObject);

        GL11.glDrawElements(GL11.GL_TRIANGLES, meshFilter.mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL13.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glDisableVertexAttribArray(3);
        GL30.glDisableVertexAttribArray(4);

        GL30.glBindVertexArray(0);
    }

}
