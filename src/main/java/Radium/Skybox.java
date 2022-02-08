package Radium;

import Radium.Graphics.*;
import Radium.Math.Matrix4;
import RadiumEditor.Console;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 * An illusion of a sky
 */
public class Skybox {

    private static Shader shader;
    private static Mesh mesh;
    private static Matrix4f projection;

    private static float skyboxScale = 1000;
    private static int skyboxTexture;

    /**
     * Skybox texture filepaths
     */
    public static String[] textures = new String[] {
            "EngineAssets/Textures/Skybox/City/1.jpg",
            "EngineAssets/Textures/Skybox/City/2.jpg",
            "EngineAssets/Textures/Skybox/City/3.jpg",
            "EngineAssets/Textures/Skybox/City/4.jpg",
            "EngineAssets/Textures/Skybox/City/5.jpg",
            "EngineAssets/Textures/Skybox/City/6.jpg",
    };
    /**
     * The skybox texture objects
     * ** Do Not Edit **
     */
    public static Texture[] individualTextures = new Texture[6];

    protected Skybox() {}

    /**
     * Initialize the shader, mesh, and textures
     */
    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/Skybox/vert.glsl", "EngineAssets/Shaders/Skybox/frag.glsl");
        mesh = Mesh.Cube(Variables.DefaultCamera.far, Variables.DefaultCamera.far);
        skyboxTexture = Texture.LoadCubeMap(textures);

        for (int i = 0; i < 6; i++) {
            individualTextures[i] = new Texture(textures[i]);
        }
    }

    /**
     * Loads cube map from texture
     * @param textures Cube map textures
     * @return GLFW cube map ID
     */
    public static int CreateCubeMap(String[] textures) {
        return Texture.LoadCubeMap(textures);
    }

    /**
     * Set the skybox texture from a cube map ID {@link}
     * @param cubeMap Texture (Use {@link #CreateCubeMap(String[]) CreateCubeMap} method to create a cube map)
     */
    public static void SetSkyboxTexture(int cubeMap) {
        skyboxTexture = cubeMap;
    }

    /**
     * Updates the skybox textures
     */
    public static void UpdateTextures() {
        GL13.glDeleteTextures(skyboxTexture);
        skyboxTexture = Texture.LoadCubeMap(textures);

        for (int i = 0; i < 6; i++) {
            individualTextures[i] = new Texture(textures[i]);
        }
    }

    /**
     * Renders the skybox
     */
    public static void Render() {
        if (Variables.DefaultCamera == null && Application.Playing) return;
        boolean cameraAvailable = Variables.DefaultCamera != null;

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        float aspect = (float)Window.width / (float)Window.height;
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

    /**
     * @return Skybox texture cube map ID
     */
    public static int GetTexture() {
        return skyboxTexture;
    }

}
