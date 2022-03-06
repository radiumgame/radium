package Radium.PostProcessing;

import Radium.Application;
import Radium.Graphics.Framebuffer.Framebuffer;
import Radium.Graphics.Shader;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Window;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class PostProcessing {

    private static Shader shader;
    private static Framebuffer framebuffer;

    private static int RECT;

    protected PostProcessing() {}

    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/PostProcessing/vert.glsl", "EngineAssets/Shaders/PostProcessing/frag.glsl");
        framebuffer = new Framebuffer(1920, 1080);

        GenerateRectangle();
    }

    public static void Render() {
        framebuffer.Bind();
        shader.Bind();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        shader.SetUniform("playing", Application.Playing);

        GL30.glBindVertexArray(RECT);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL13.glActiveTexture(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Window.GetFrameBuffer().GetTextureID());
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        shader.Unbind();
        framebuffer.Unbind();
    }

    public static int GetTexture() {
        return framebuffer.GetTextureID();
    }

    private static void GenerateRectangle() {
        Vector3[] vertices = {
            new Vector3(-1, -1, 0),
            new Vector3(1, -1, 0),
            new Vector3(-1, 1, 0),

            new Vector3(-1, 1, 0),
            new Vector3(1, 1, 0),
            new Vector3(1, -1, 0),
        };
        Vector2[] textureCoords = {
            new Vector2(0, 0),
            new Vector2(1, 0),
            new Vector2(0, 1),

            new Vector2(0, 1),
            new Vector2(1, 1),
            new Vector2(1, 0),
        };

        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
        float[] positionData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            positionData[i * 3] = vertices[i].x;
            positionData[i * 3 + 1] = vertices[i].y;
            positionData[i * 3 + 2] = vertices[i].z;
        }
        positionBuffer.put(positionData).flip();

        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(textureCoords.length * 2);
        float[] textureData = new float[textureCoords.length * 2];
        for (int i = 0; i < textureCoords.length; i++) {
            textureData[i * 2] = textureCoords[i].x;
            textureData[i * 2 + 1] = textureCoords[i].y;
        }
        textureBuffer.put(textureData).flip();

        RECT = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(RECT);
        StoreData(positionBuffer, 0, 3);
        StoreData(textureBuffer, 1, 2);
    }

    private static void StoreData(FloatBuffer buffer, int index, int size)
    {
        int bufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

}
