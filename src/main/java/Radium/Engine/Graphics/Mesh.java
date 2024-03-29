package Radium.Engine.Graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import Radium.Editor.Console;
import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.FrustumCulling.AABB;
import Radium.Engine.Math.Mathf;
import Radium.Engine.Math.Vector.*;
import Radium.Engine.ModelLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.meshoptimizer.MeshOptimizer;
import org.lwjgl.util.par.ParShapes;
import org.lwjgl.util.par.ParShapesMesh;

/**
 * Mesh settings
 */
public class Mesh {

	//region Mesh
	public final Vertex[] vertices;
	public final int[] indices;
	private transient int vao, pbo, ibo, tbo;

	private int MeshID;

	private transient boolean created = false;

	/**
	 * Create mesh with predefined vertices
	 * @param vertices Vertices of mesh
	 * @param indices Indices/Triangles of mesh
	 */
	public Mesh(Vertex[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;

		CreateMesh();
	}

	/**
	 * Creates the mesh VAO
	 */
	public void CreateMesh() {
		if (created) {
			Destroy();
		}

		final int prime = 31;
		MeshID = 1;
		for (Vertex v : vertices) {
			MeshID = prime * MeshID + Float.floatToIntBits(v.hashCode());
		}

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

		created = true;
	}

	/**
	 * Calculates normals of mesh
	 */
	public void RecalculateNormals() {
		for (int i = 0; i < indices.length; i += 3) {
			Vertex a = vertices[indices[i]];
			Vertex b = vertices[indices[i+ 1]];
			Vertex c = vertices[indices[i + 2]];

			Vector3 edge1 = Vector3.Subtract(c.GetPosition(), b.GetPosition());
			Vector3 edge2 = Vector3.Subtract(a.GetPosition(), b.GetPosition());
			Vector3 normal = Vector3.Cross(edge1, edge2);

			a.SetNormal(Vector3.Normalized(Vector3.Add(a.GetNormal(), normal)));
			b.SetNormal(Vector3.Normalized(Vector3.Add(b.GetNormal(), normal)));
			c.SetNormal(Vector3.Normalized(Vector3.Add(c.GetNormal(), normal)));
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

	/**
	 * Destroys the mesh's buffer
	 */
	public void DestroyBuffers() {
		GL15.glDeleteBuffers(pbo);
		GL15.glDeleteBuffers(ibo);
		GL15.glDeleteBuffers(tbo);
		GL30.glDeleteVertexArrays(vao);

		vao = 0;
		ibo = 0;
	}

	/**
	 * Destroy the mesh
	 */
	public void Destroy() {
		if (!created) return;

		DestroyBuffers();
		created = false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj.getClass() != this.getClass()) return false;
		Mesh other = (Mesh) obj;
		return other.MeshID == this.MeshID;
	}

	/**
	 * Returns the mesh vertices
	 * @return Mesh vertices
	 */
	public Vertex[] GetVertices() {
		return vertices;
	}

	/**
	 * Returns the mesh indices
	 * @return Mesh indices
	 */
	public int[] GetIndices() {
		return indices;
	}

	/**
	 * Returns the mesh VAO
	 * @return Mesh VAO
	 */
	public int GetVAO() {
		return vao;
	}

	/**
	 * Returns mesh PBO
	 * @return Mesh PBO
	 */
	public int GetPBO() {
		return pbo;
	}

	/**
	 * Returns mesh TBO
	 * @return Mesh TBO
	 */
	public int GetTBO() {
		return tbo;
	}

	/**
	 * Returns mesh IBO
	 * @return Mesh IBO
	 */
	public int GetIBO() {
		return ibo;
	}

	/**
	 * Returns whether the mesh has been created
	 * @return
	 */
	public boolean Created() { return created; }


	//endregion

	//region Mesh Types

	public static Mesh Empty() {
		Vertex[] vertices = new Vertex[0];
		int[] indices = new int[0];

		return new Mesh(vertices, indices);
	}

	/**
	 * Creates a cube mesh(no tangents or bitangents)
	 * @param blockWidth Cube width
	 * @param blockHeight Cube height
	 * @return New cube mesh
	 */
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

	public static Mesh Plane() {
		ParShapesMesh sm = ParShapes.par_shapes_create_plane(1, 1);
		ParShapes.par_shapes_translate(sm, -0.5f, -0.5f, 0);
		return GetMesh(sm);
	}

	/**
	 * Creates cube mesh
	 * @return New cube mesh
	 */
	public static Mesh Cube() {
		Mesh mesh = ModelLoader.LoadModelNoMultiThread("EngineAssets/Models/Cube.fbx", false).GetChildren().get(0).GetChildren().get(0).GetComponent(MeshFilter.class).mesh;
		return mesh;
	}

	public static Mesh Sphere(float radius, int subdivisions) {
		ParShapesMesh sm = ParShapes.par_shapes_create_subdivided_sphere(subdivisions);
		ParShapes.par_shapes_scale(sm, radius, radius, radius);
		return GetMesh(sm);
	}

	public static Mesh GetMesh(ParShapesMesh sm) {
		ParShapes.par_shapes_rotate(sm, Mathf.Radians(-90), new float[] { 1, 0, 0 });

		Vertex[] vertices = new Vertex[sm.npoints()];
		FloatBuffer points = sm.points(sm.npoints() * 3);
		FloatBuffer normals = sm.normals(sm.npoints() * 3);
		FloatBuffer texCoords = sm.tcoords(sm.npoints() * 2);

		for (int i = 0; i < vertices.length; i++) {
			Vector3 position = new Vector3(points.get(i * 3), points.get(i * 3 + 1), points.get(i * 3 + 2));
			Vector3 normal = new Vector3(normals.get(i * 3), normals.get(i * 3 + 1), normals.get(i * 3 + 2));

			Vector2 texCoord = Vector2.Zero();
			if (texCoords != null) {
				texCoord = new Vector2(texCoords.get(i * 2), texCoords.get(i * 2 + 1));
			}

			vertices[i] = new Vertex(position, normal, texCoord);
		}

		int[] indices = new int[sm.ntriangles() * 3];
		IntBuffer triangles = sm.triangles(sm.ntriangles() * 3);
		triangles.get(indices);

		ParShapes.par_shapes_free_mesh(sm);

		return new Mesh(vertices, indices);
	}

	/**
	 * Creates plane mesh
	 * @param width Plane width
	 * @param length Plane length
	 * @return New plane mesh
	 */
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