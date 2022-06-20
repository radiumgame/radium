package Radium.Graphics.Renderers;

import Radium.Application;
import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Rendering.Light;
import Radium.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Graphics.Shader.Shader;
import Radium.Math.Matrix4;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.Variables;
import RadiumEditor.LocalEditorSettings;
import org.lwjgl.opengl.*;

/**
 * A base rendering class with basic rendering implementation
 */
public abstract class Renderer {

    public Shader shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
    private final Shader outline = new Shader("EngineAssets/Shaders/Outline/vert.glsl", "EngineAssets/Shaders/Outline/frag.glsl");

    protected final float outlineWidth = 0.08f;
    protected final Vector3 outlineColor = new Vector3(1f, 0.78f, 0.3f);

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

        boolean outline = Outline(gameObject, meshFilter, outlineWidth, outlineColor);
        if (outline) return;

        GL11.glEnable(GL11.GL_DEPTH_TEST);
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

        if (Light.lightsInScene.size() > 0) {
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
            GL13.glBindTexture(GL11.GL_TEXTURE_2D, Light.lightsInScene.get(0).shadowFramebuffer.GetDepthMap());
        }

        shader.Bind();

        shader.SetUniform("depthTestFrame", DepthFramebuffer.DepthTesting);

        shader.SetUniform("color", meshFilter.material.color.ToVector3());

        shader.SetUniform("model", Matrix4.Transform(gameObject.transform));
        shader.SetUniform("view", Application.Playing ? Variables.DefaultCamera.GetView() : Variables.EditorCamera.GetView());
        shader.SetUniform("projection", Application.Playing ? Variables.DefaultCamera.GetProjection() : Variables.EditorCamera.GetProjection());

        shader.SetUniform("tex", 0);
        shader.SetUniform("normalMap", 1);
        shader.SetUniform("specularMap", 2);
        if (Light.lightsInScene.size() > 0) {
            shader.SetUniform("lightDepth", 3);
        }

        SetUniforms(gameObject);

        GL11.glDrawElements(GL11.GL_TRIANGLES, meshFilter.mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        //shader.Unbind();

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

    protected boolean Outline(GameObject obj, MeshFilter meshFilter, float outlineWidth, Vector3 outlineColor) {
        if (!meshFilter.selected && LocalEditorSettings.RenderState != GL11.GL_LINE) {
            return false;
        }

        if (Application.Playing) {
            return false;
        }

        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
        GL11.glStencilMask(0x00);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(meshFilter.mesh.GetVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);
        GL30.glEnableVertexAttribArray(4);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshFilter.mesh.GetIBO());
        outline.Bind();

        outline.SetUniform("model", Matrix4.Transform(obj.transform));
        outline.SetUniform("view", Application.Playing ? Variables.DefaultCamera.GetView() : Variables.EditorCamera.GetView());
        outline.SetUniform("projection", Application.Playing ? Variables.DefaultCamera.GetProjection() : Variables.EditorCamera.GetProjection());
        outline.SetUniform("outline", outlineWidth);
        outline.SetUniform("color", outlineColor);
        shader.SetUniform("use", LocalEditorSettings.RenderState == GL11.GL_LINE);

        GL11.glDrawElements(GL11.GL_TRIANGLES, meshFilter.mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        outline.Unbind();
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glDisableVertexAttribArray(3);
        GL30.glDisableVertexAttribArray(4);
        GL30.glBindVertexArray(0);

        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        return LocalEditorSettings.RenderState == GL11.GL_LINE;
    }

}
