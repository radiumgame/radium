package Radium.Engine.Skybox;

import Radium.Engine.Application;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Matrix4;
import Radium.Engine.Variables;
import Radium.Engine.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.io.File;

public class CubemapSkybox {

    private static Shader shader;
    private static Mesh mesh;
    private static Matrix4f projection;

    private static final float skyboxScale = 10000;
    private static int skyboxTexture;

    public static String[] textures = new String[] {
            "EngineAssets/Textures/Skybox/City/1.jpg",
            "EngineAssets/Textures/Skybox/City/2.jpg",
            "EngineAssets/Textures/Skybox/City/3.jpg",
            "EngineAssets/Textures/Skybox/City/4.jpg",
            "EngineAssets/Textures/Skybox/City/5.jpg",
            "EngineAssets/Textures/Skybox/City/6.jpg",
    };
    public static File[] individualTextures = new File[6];

    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Skybox/vert.glsl", "EngineAssets/Shaders/Skybox/frag.glsl");
        mesh = Mesh.Cube(skyboxScale, skyboxScale);
        skyboxTexture = Texture.LoadCubeMap(textures);

        for (int i = 0; i < 6; i++) {
            individualTextures[i] = new File(textures[i]);
        }
    }

    public static int CreateCubeMap(String[] textures) {
        return Texture.LoadCubeMap(textures);
    }

    public static void SetSkyboxTexture(int cubeMap) {
        skyboxTexture = cubeMap;
    }

    public static void UpdateTextures() {
        GL13.glDeleteTextures(skyboxTexture);
        skyboxTexture = Texture.LoadCubeMap(textures);

        for (int i = 0; i < 6; i++) {
            individualTextures[i] = new File(textures[i]);
        }
    }

    public static void Render() {
        if (Variables.DefaultCamera == null && Application.Playing) return;
        boolean cameraAvailable = Variables.DefaultCamera != null && Variables.DefaultCamera.gameObject != null;

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        float aspect = (float) Window.width / (float)Window.height;
        projection = new Matrix4f().perspective((float)Math.toRadians(Application.Playing ? Variables.DefaultCamera.fov : 70), aspect, 0.1f, skyboxScale + 1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skyboxTexture);

        shader.Bind();

        Matrix4f view = Matrix4.View(Application.Playing ? (cameraAvailable ? Variables.DefaultCamera.gameObject.transform : Variables.EditorCamera.transform) : Variables.EditorCamera.transform, Application.Playing);
        view.m30(0);
        view.m31(0);
        view.m32(0);

        shader.SetUniform("view", view);
        shader.SetUniform("projection", projection);

        shader.SetUniform("tex", 0);

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    public static int GetTexture() {
        return skyboxTexture;
    }

}
