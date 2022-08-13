package Radium.Engine.Graphics.Renderers;

import Radium.Engine.Application;
import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Components.Rendering.Light;
import Radium.Engine.Graphics.Shader.ShaderUniform;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Matrix4;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Time;
import Radium.Engine.Variables;
import Radium.Engine.Window;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class CustomRenderer extends Renderer {

    public void Initialize() {

    }

    public void SetUniforms(GameObject gameObject) {
        if (shader == null) return;

        shader.SetUniform("gameObject.position", gameObject.transform.WorldPosition());
        shader.SetUniform("gameObject.rotation", gameObject.transform.WorldRotation());
        shader.SetUniform("gameObject.scale", gameObject.transform.WorldScale());
        shader.SetUniform("gameObject.localPosition", gameObject.transform.localPosition);
        shader.SetUniform("gameObject.localRotation", gameObject.transform.localRotation);
        shader.SetUniform("gameObject.localScale", gameObject.transform.localScale);

        shader.SetUniform("cameraPosition", Application.Playing ? Variables.DefaultCamera.gameObject.transform.WorldPosition() : Variables.EditorCamera.transform.position);
        shader.SetUniform("light", Light.currentIndex);
        shader.SetUniform("numLights", Light.lightsInScene.size());

        shader.SetUniform("time", Time.GetTime());
        shader.SetUniform("deltaTime", Time.deltaTime);
        shader.SetUniform("viewDirection", (Application.Playing) ? Variables.DefaultCamera.gameObject.transform.Forward() : Variables.EditorCamera.transform.EditorForward());
        shader.SetUniform("resolution", new Vector2(Window.width, Window.height));

        shader.SetUniform("lightCount",Light.LightIndex + 1);
        for (Light light : Light.lightsInScene) {
            if (light == null || light.gameObject == null) continue;

            shader.SetUniform("lights[" + light.index + "].position", light.gameObject.transform.WorldPosition());
            shader.SetUniform("lights[" + light.index + "].color", light.color.ToVector3());
            shader.SetUniform("lights[" + light.index + "].intensity", light.intensity);
            shader.SetUniform("lights[" + light.index + "].attenuation", light.attenuation);
            shader.SetUniform("lights[" + light.index + "].type", light.lightType.ordinal());
        }
    }

    public void Render(GameObject gameObject) {
        if (!gameObject.ContainsComponent(MeshFilter.class)) return;
        if (Variables.DefaultCamera == null && Application.Playing) return;

        MeshFilter meshFilter = gameObject.GetComponent(MeshFilter.class);
        if (meshFilter.mesh == null) return;

        Outline(gameObject, meshFilter, outlineColor);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL30.glBindVertexArray(meshFilter.mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshFilter.mesh.GetIBO());

        for (int i = 0; i < shader.uniforms.size(); i++) {
            ShaderUniform uniform = shader.uniforms.get(i);
            if (uniform.type == Texture.class && uniform.value != null) {
                uniform.UpdateType();
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
                GL13.glBindTexture(GL11.GL_TEXTURE_2D, ((Texture)uniform.value).textureID);
                uniform.temp = i;
            }
        }

        shader.Bind();

        shader.SetUniform("model", Matrix4.Transform(gameObject.transform));
        shader.SetUniform("view", Application.Playing ? Variables.DefaultCamera.GetView() : Variables.EditorCamera.GetView());
        shader.SetUniform("projection", Application.Playing ? Variables.DefaultCamera.GetProjection() : Variables.EditorCamera.GetProjection());

        SetUniforms(gameObject);
        for (ShaderUniform uniform : shader.GetUniforms()) {
            if (uniform.type == Texture.class) {
                if (uniform.value != null) {
                    shader.SetUniform(uniform.name, uniform.temp);
                    uniform.temp = 0;
                }
            } else {
                uniform.Set();
            }
        }

        shader.Validate();
        GL11.glDrawElements(GL11.GL_TRIANGLES, meshFilter.mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        //shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL13.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);

        GL30.glBindVertexArray(0);
    }

}
