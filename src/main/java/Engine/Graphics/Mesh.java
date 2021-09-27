package Engine.Graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import Engine.Math.Vector.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class Mesh {

	//region Mesh
	private Vertex[] vertices;
	private int[] indices;
	public Material material;
	private int vao, pbo, ibo, cbo, tbo;

	private boolean created = false;
	
	public Mesh(Vertex[] vertices, int[] indices, Material material) {
		this.vertices = vertices;
		this.indices = indices;
		this.material = material;
	}
	
	public void CreateMesh() {
		material.CreateMaterial();

		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

		FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] positionData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			positionData[i * 3] = vertices[i].GetPosition().x;
			positionData[i * 3 + 1] = vertices[i].GetPosition().y;
			positionData[i * 3 + 2] = vertices[i].GetPosition().z;
		}
		positionBuffer.put(positionData).flip();

		pbo = StoreData(positionBuffer, 0, 3);

		FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
		float[] textureData = new float[vertices.length * 2];
		for (int i = 0; i < vertices.length; i++) {
			textureData[i * 2] = vertices[i].GetTextureCoordinates().x;
			textureData[i * 2 + 1] = vertices[i].GetTextureCoordinates().y;
		}
		textureBuffer.put(textureData).flip();

		tbo = StoreData(textureBuffer, 1, 2);

		IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		indicesBuffer.put(indices).flip();

		ibo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] normalData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			normalData[i * 3] = vertices[i].GetNormal().x;
			normalData[i * 3 + 1] = vertices[i].GetNormal().y;
			normalData[i * 3 + 2] = vertices[i].GetNormal().z;
		}
		normalBuffer.put(normalData).flip();

		StoreData(normalBuffer, 23, 3);

		created = true;
	}
	
	private int StoreData(FloatBuffer buffer, int index, int size)
	{
		int bufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		return bufferID;
	}
	
	public void DestroyBuffers() {
		GL15.glDeleteBuffers(pbo);
		GL15.glDeleteBuffers(cbo);
		GL15.glDeleteBuffers(ibo);
		GL15.glDeleteBuffers(tbo);
		GL30.glDeleteVertexArrays(vao);
		
		material.DestroyMaterial();
	}
	
	public void DestroyMesh() {
		material.DestroyMaterial();
		DestroyBuffers();
	}

	public Vertex[] GetVertices() {
		return vertices;
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
	
	public int GetCBO() {
		return cbo;
	}
	
	public int GetTBO() {
		return tbo;
	}

	public int GetIBO() {
		return ibo;
	}

	public boolean Created() { return created; }
	
	public Material GetMaterial() {
		return material;
	}

	//endregion

	//region Mesh Types

	public static Mesh Cube(float blockWidth, float blockHeight, String texturePath) {
		float width = blockWidth / 2;
		float height = blockHeight / 2;

		Mesh mesh = new Mesh(new Vertex[] {
				//Back face
				new Vertex(new Vector3(-width,  height, -width), new Vector3(0, 0, -1), new Vector2(0.0f, 0.0f)),
				new Vertex(new Vector3(-width, -height, -width), new Vector3(0, 0, -1), new Vector2(0.0f, 1.0f)),
				new Vertex(new Vector3(width, -height, -width), new Vector3(0, 0, -1), new Vector2(1.0f, 1.0f)),
				new Vertex(new Vector3(width,  height, -width), new Vector3(0, 0, -1), new Vector2(1.0f, 0.0f)),

				//Front face
				new Vertex(new Vector3(-width,  height,  width), new Vector3(0, 0, 1), new Vector2(0.0f, 0.0f)),
				new Vertex(new Vector3(-width, -height,  width), new Vector3(0, 0, 1), new Vector2(0.0f, 1.0f)),
				new Vertex(new Vector3(width, -height,  width), new Vector3(0, 0, 1), new Vector2(1.0f, 1.0f)),
				new Vertex(new Vector3(width,  height,  width), new Vector3(0, 0, 1), new Vector2(1.0f, 0.0f)),

				//Right face
				new Vertex(new Vector3(width,  height, -width), new Vector3(1, 0, 0), new Vector2(0.0f, 0.0f)),
				new Vertex(new Vector3(width, -height, -width), new Vector3(1, 0, 0), new Vector2(0.0f, 1.0f)),
				new Vertex(new Vector3(width, -height,  width), new Vector3(1, 0, 0), new Vector2(1.0f, 1.0f)),
				new Vertex(new Vector3(width,  height,  width), new Vector3(1, 0, 0), new Vector2(1.0f, 0.0f)),

				//Left face
				new Vertex(new Vector3(-width,  height, -width), new Vector3(-1, 0, 0), new Vector2(0.0f, 0.0f)),
				new Vertex(new Vector3(-width, -height, -width), new Vector3(-1, 0, 0), new Vector2(0.0f, 1.0f)),
				new Vertex(new Vector3(-width, -height,  width), new Vector3(-1, 0, 0), new Vector2(1.0f, 1.0f)),
				new Vertex(new Vector3(-width,  height,  width), new Vector3(-1, 0, 0), new Vector2(1.0f, 0.0f)),

				//Top face
				new Vertex(new Vector3(-width,  height,  width), new Vector3(0, 1, 0), new Vector2(0.0f, 0.0f)),
				new Vertex(new Vector3(-width,  height, -width), new Vector3(0, 1, 0), new Vector2(0.0f, 1.0f)),
				new Vertex(new Vector3(width,  height, -width), new Vector3(0, 1, 0), new Vector2(1.0f, 1.0f)),
				new Vertex(new Vector3(width,  height,  width), new Vector3(0, 1, 0), new Vector2(1.0f, 0.0f)),

				//Bottom face
				new Vertex(new Vector3(-width, -height,  width), new Vector3(0, -1, 0), new Vector2(0.0f, 0.0f)),
				new Vertex(new Vector3(-width, -height, -width), new Vector3(0, -1, 0), new Vector2(0.0f, 1.0f)),
				new Vertex(new Vector3(width, -height, -width), new Vector3(0, -1, 0), new Vector2(1.0f, 1.0f)),
				new Vertex(new Vector3(width, -height,  width), new Vector3(0, -1, 0), new Vector2(1.0f, 0.0f)),
		}, new int[] {
				//Back face
				0, 1, 3,
				3, 1, 2,

				//Front face
				4, 5, 7,
				7, 5, 6,

				//Right face
				8, 9, 11,
				11, 9, 10,

				//Left face
				12, 13, 15,
				15, 13, 14,

				//Top face
				16, 17, 19,
				19, 17, 18,

				//Bottom face
				20, 21, 23,
				23, 21, 22
		}, new Material(texturePath));

		return mesh;
	}

	public static Mesh Plane(float width, float length, String texturePath) {
		float halfOfWidth = width / 2;
		float halfOfLength = length / 2;

		Mesh mesh = new Mesh(new Vertex[] {
				new Vertex(new Vector3(-halfOfWidth, 0,  halfOfLength), new Vector2(0.0f, 0.0f)),
				new Vertex(new Vector3(-halfOfWidth, 0, -halfOfLength), new Vector2(0.0f, 1.0f)),
				new Vertex(new Vector3(halfOfWidth, 0, -halfOfLength), new Vector2(1.0f, 1.0f)),
				new Vertex(new Vector3(halfOfWidth, 0,  halfOfLength), new Vector2(1.0f, 0.0f)),
		}, new int[] {
				0, 1, 3,
				3, 1, 2
		}, new Material(texturePath));

		return mesh;
	}

	public static Mesh Terrain(int width, int length, float[][] noise, String texturePath) {
		Mesh terrain = new Mesh(null, null, new Material(texturePath));

		Vector3[] vertices = new Vector3[(width + 1) * (length + 1)];
		int[] indices = new int[width * length * 6];
		Vector2[] uvs = new Vector2[vertices.length];

		for (int i = 0, x = 0; x <= width; x++)
		{
			for (int z = 0; z <= length; z++)
			{
				float y = noise[x][z];
				vertices[i] = new Vector3(x, y, z);

				i++;
			}
		}

		int vert = 0;
		int tris = 0;
		for (int x = 0; x < width; x++)
		{
			for (int z = 0; z < length; z++)
			{
				indices[tris + 5] = vert + 0;
				indices[tris + 4] = vert + width + 1;
				indices[tris + 3] = vert + 1;
				indices[tris + 2] = vert + 1;
				indices[tris + 1] = vert + width + 1;
				indices[tris + 0] = vert + width + 2;

				vert++;
				tris += 6;
			}
			vert++;
		}

		for (int i = 0, x = 0; x <= width; x++)
		{
			for (int z = 0; z <= length; z++)
			{
				uvs[i] = new Vector2((float)x / width, (float)z / length);

				i++;
			}
		}

		Vertex[] realVertices = new Vertex[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			realVertices[i] = new Vertex(vertices[i], uvs[i]);
		}

		terrain.vertices = realVertices;
		terrain.indices = indices;

		return terrain;
	}

	//endregion
}