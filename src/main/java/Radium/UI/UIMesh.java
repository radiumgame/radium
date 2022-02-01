package Radium.UI;

import Radium.Color;
import Radium.Graphics.Mesh;
import Radium.Graphics.Texture;
import Radium.Math.Vector.Vector2;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class UIMesh {

    public Vector2[] vertices;
    public Vector2[] textureCoords;
    public int[] indices;

    public Texture texture;
    public Color color;

    private int vao, pbo, ibo, tbo;
    private boolean created = false;

    public UIMesh(Vector2[] vertices, int[] indices) {
        this.vertices = vertices;
        this.textureCoords = new Vector2[0];
        this.indices = indices;
        this.texture = new Texture("EngineAssets/Textures/Misc/blank.jpg");
        this.color = new Color(255, 255, 255, 255);

        CreateMesh();
    }

    public UIMesh(Vector2[] vertices, Vector2[] textureCoords, int[] indices) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.texture = new Texture("EngineAssets/Textures/Misc/blank.jpg");
        this.color = new Color(255, 255, 255, 255);

        CreateMesh();
    }

    public UIMesh(Vector2[] vertices, Vector2[] textureCoords, int[] indices, String texture) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.texture = new Texture(texture);
        this.color = new Color(255, 255, 255, 255);

        CreateMesh();
    }

    private void CreateMesh() {
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
        float[] positionData = new float[vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            positionData[i * 2] = vertices[i].x;
            positionData[i * 2 + 1] = vertices[i].y;
        }
        positionBuffer.put(positionData).flip();
        pbo = StoreData(positionBuffer, 0, 2);

        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
        float[] textureData = new float[vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            textureData[i * 2] = textureCoords[i].x;
            textureData[i * 2 + 1] = textureCoords[i].y;
        }
        textureBuffer.put(textureData).flip();
        tbo = StoreData(textureBuffer, 1, 2);

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();

        ibo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        created = true;
    }

    public void Destroy() {
        GL15.glDeleteBuffers(vao);
        GL15.glDeleteBuffers(pbo);
        GL15.glDeleteBuffers(tbo);
        GL15.glDeleteBuffers(ibo);
    }

    public Vector2[] GetVertices() {
        return vertices;
    }

    public Vector2[] GetTextureCoordinates() {
        return textureCoords;
    }

    public int[] GetIndices() {
        return indices;
    }

    public int GetVAO() {
        return vao;
    }

    public int GetPBO() {
        return pbo;
    }

    public int GetIBO() {
        return ibo;
    }

    public int GetTBO() {
        return tbo;
    }

    public boolean IsCreated() {
        return created;
    }

    private int StoreData(FloatBuffer buffer, int index, int size) {
        int bufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        return bufferID;
    }

    public static UIMesh Quad() {
        Vector2[] verts = new Vector2[] {
                new Vector2(-0.5f, 0.5f),
                new Vector2(0.5f, 0.5f),
                new Vector2(-0.5f, -0.5f),
                new Vector2(0.5f, -0.5f)
        };
        Vector2[] coords = new Vector2[] {
                new Vector2(0, 0),
                new Vector2(1, 0),
                new Vector2(0, 1),
                new Vector2(1, 1),
        };
        int[] indices = new int[] {
                0, 1, 2, 3, 2, 1
        };

        return new UIMesh(verts, coords, indices, "EngineAssets/Textures/Misc/box.jpg");
    }

}
