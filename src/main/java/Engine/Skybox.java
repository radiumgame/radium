package Engine;

import Engine.Components.Graphics.MeshFilter;
import Engine.Graphics.*;
import Engine.Math.Matrix4;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector3;
import Engine.Objects.GameObject;
import Engine.Util.NonInstantiatable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public final class Skybox extends NonInstantiatable {

    private static Shader shader;
    private static Mesh mesh;
    private static Matrix4f projection;

    private static float skyboxScale;

    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Unlit/vert.glsl", "EngineAssets/Shaders/Unlit/frag.glsl");
        mesh = ModelLoader.LoadModel("EngineAssets/Sphere.fbx", "EngineAssets/Textures/Skybox/Skybox.jpg")[0];
    }

    public static void SetSkyboxTexture(Texture texture) {
        Material material = new Material(texture.filepath);
        material.CreateMaterial();

        mesh.material = material;
    }

    public static void Render() {
        if (Variables.DefaultCamera == null && Application.Playing) return;

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        skyboxScale = Variables.DefaultCamera.far;
        float aspect = (float)Window.width / (float)Window.height;
        projection = new Matrix4f().perspective((float)Math.toRadians(Application.Playing ? Variables.DefaultCamera.fov : 70), aspect, 0.1f, skyboxScale + 1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, mesh.GetMaterial().GetTextureID());

        shader.Bind();

        Transform transform = new Transform();
        transform.position = Application.Playing ? Variables.DefaultCamera.gameObject.transform.position : Variables.EditorCamera.transform.position;
        transform.rotation = new Vector3(90, 0, 0);
        transform.scale = new Vector3(skyboxScale, skyboxScale, skyboxScale);

        shader.SetUniform("model", Matrix4.Transform(transform));
        shader.SetUniform("view", Matrix4.View(Application.Playing ? Variables.DefaultCamera.gameObject.transform : Variables.EditorCamera.transform));
        shader.SetUniform("projection", projection);

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

}
