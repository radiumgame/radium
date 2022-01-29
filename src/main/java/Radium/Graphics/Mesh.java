package Radium.Graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import Radium.Math.Vector.*;
import Radium.ModelLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class Mesh {

	//region Mesh
	private Vertex[] vertices;
	private int[] indices;
	private transient int vao, pbo, ibo, tbo;

	private transient boolean created = false;
	
	public Mesh(Vertex[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;

		CreateMesh();
	}
	
	public void CreateMesh() {
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
		StoreData(normalBuffer, 2, 3);

		FloatBuffer tangentBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] tangentData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			tangentData[i * 3] = vertices[i].GetTangent().x;
			tangentData[i * 3 + 1] = vertices[i].GetTangent().y;
			tangentData[i * 3 + 2] = vertices[i].GetTangent().z;
		}
		tangentBuffer.put(tangentData).flip();
		StoreData(tangentBuffer, 3, 3);

		FloatBuffer bitangentBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] bitangentData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			bitangentData[i * 3] = vertices[i].GetBitangent().x;
			bitangentData[i * 3 + 1] = vertices[i].GetBitangent().y;
			bitangentData[i * 3 + 2] = vertices[i].GetBitangent().z;
		}
		bitangentBuffer.put(bitangentData).flip();
		StoreData(bitangentBuffer, 4, 3);

		created = true;
	}

	public void RecalculateNormals() {
		Vector3[] normals = new Vector3[vertices.length];
		for (int i = 0; i < normals.length; i++) {
			normals[i] = vertices[i].GetNormal();
		}

		try {
			for (int i = 0; i < indices.length / 3; i += 3) {
				Vector3 a = vertices[i].GetPosition();
				Vector3 b = vertices[i + 1].GetPosition();
				Vector3 c = vertices[i + 2].GetPosition();

				Vector3 edge1 = Vector3.Subtract(b, a);
				Vector3 edge2 = Vector3.Subtract(c, a);
				Vector3 normal = Vector3.Cross(edge1, edge2);
				Vector3 weightedNormal = Vector3.Add(vertices[i].GetNormal(), normal);

				vertices[i].SetNormal(weightedNormal);
				vertices[i + 1].SetNormal(weightedNormal);
				vertices[i + 2].SetNormal(weightedNormal);
			}
			for (Vertex vertex : vertices) {
				vertex.SetNormal(Vector3.Normalized(vertex.GetNormal()));
			}
		} catch (Exception e) {
			for (int i = 0; i < normals.length; i++) {
				vertices[i].SetNormal(normals[i]);
			}
		}
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
		GL15.glDeleteBuffers(ibo);
		GL15.glDeleteBuffers(tbo);
		GL30.glDeleteVertexArrays(vao);
	}
	
	public void DestroyMesh() {
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
	
	public int GetTBO() {
		return tbo;
	}

	public int GetIBO() {
		return ibo;
	}

	public boolean Created() { return created; }


	//endregion

	//region Mesh Types

	public static Mesh Cube(float blockWidth, float blockHeight) {
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
		});

		return mesh;
	}

	public static Mesh Cube() {
		Mesh mesh = ModelLoader.LoadModel("EngineAssets/Models/Cube.fbx")[0];
		return mesh;
	}

	public static Mesh Plane(float width, float length) {
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
		});

		return mesh;
	}

	//endregion
}