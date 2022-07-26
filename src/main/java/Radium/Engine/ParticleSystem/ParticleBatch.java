package Radium.Engine.ParticleSystem;

import Radium.Engine.Components.Particles.ParticleSystem;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Graphics.Vertex;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.GameObject;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ParticleBatch {

    public transient List<Particle> particles = new ArrayList<>();
    public transient Texture texture;

    public transient int mesh;
    public transient int meshVbo;
    public transient int meshIbo;
    public transient int indicesCount;
    private transient FloatBuffer buffer;

    public transient GameObject obj;

    public final int DataLength = 24;

    public transient ParticleSystem system;

    public ParticleBatch(ParticleSystem system, Texture texture) {
        this.system = system;
        this.texture = texture;

        buffer = BufferUtils.createFloatBuffer(system.maxParticles * DataLength);
        GenerateMesh();
    }

    public void ResizeBuffer() {
        buffer = BufferUtils.createFloatBuffer(system.maxParticles * DataLength);
    }

    public void Update() {
        ParticleSorter.SortHighToLow(particles);

        if (particles.size() > system.maxParticles) {
            for (int i = 1; i <= particles.size() - system.maxParticles; i++) {
                particles.remove(particles.size() - i);
            }
        }
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).Update();
        }
    }

    private void GenerateMesh() {
        Vertex[] vertices = new Vertex[] {
                new Vertex(new Vector3(-0.5f, 0,  0.5f), new Vector2(0.0f, 0.0f)),
                new Vertex(new Vector3(-0.5f, 0, -0.5f), new Vector2(0.0f, 1.0f)),
                new Vertex(new Vector3(0.5f, 0, -0.5f), new Vector2(1.0f, 1.0f)),
                new Vertex(new Vector3(0.5f, 0,  0.5f), new Vector2(1.0f, 0.0f)),
        };
        int[] indices = new int[] { 0, 1, 3, 3, 1, 2 };
        indicesCount = 6;

        meshVbo = CreateEmptyVBO(system.maxParticles * DataLength);
        mesh = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(mesh);

        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
        float[] positionData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            positionData[i * 3] = vertices[i].GetPosition().x;
            positionData[i * 3 + 1] = vertices[i].GetPosition().y;
            positionData[i * 3 + 2] = vertices[i].GetPosition().z;
        }
        positionBuffer.put(positionData).flip();
        StoreData(positionBuffer, 0, 3);

        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
        float[] textureData = new float[vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            textureData[i * 2] = vertices[i].GetTextureCoordinates().x;
            textureData[i * 2 + 1] = vertices[i].GetTextureCoordinates().y;
        }
        textureBuffer.put(textureData).flip();
        StoreData(textureBuffer, 1, 2);

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();

        meshIbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, meshIbo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        AddAttribute(meshVbo, 2, 4, 0);
        AddAttribute(meshVbo, 3, 4, 4);
        AddAttribute(meshVbo, 4, 4, 8);
        AddAttribute(meshVbo, 5, 4, 12);
        AddAttribute(meshVbo, 6, 4, 16);
        AddAttribute(meshVbo, 7, 3, 20);
        AddAttribute(meshVbo, 8, 1, 23);
    }

    private void StoreData(FloatBuffer buffer, int index, int size)
    {
        int bufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private int CreateEmptyVBO(int floatCount) {
        int bufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        return bufferID;
    }

    private void AddAttribute(int vbo, int attribute, int dataSize, int offset) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, DataLength * 4, offset * 4);
        GL33.glVertexAttribDivisor(attribute, 1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void UpdateVBO(float[] data) {
        if (data.length > buffer.capacity()) {
            return;
        }

        buffer.clear();
        buffer.put(data);
        buffer.flip();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, meshVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity(), GL15.GL_STREAM_DRAW);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

}
