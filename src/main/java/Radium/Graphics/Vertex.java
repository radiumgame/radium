package Radium.Graphics;

import Radium.Math.Vector.*;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIVertexWeight;

import java.util.ArrayList;
import java.util.List;

/**
 * A mesh vertex
 */
public class Vertex {

	private Vector3 position;
	private Vector3 normal;
	private Vector3 tangent = Vector3.Zero();
	private Vector3 bitangent = Vector3.Zero();
	private Vector2 textureCoord;

	private AIBone bone;
	private List<Float> weights = new ArrayList<>();

	/**
	 * Create a vertex with a position, normal, and texture coordinate
	 * @param position Vertex position
	 * @param normal Vertex normal
	 * @param textureCoord Vertex texture coordinate
	 */
	public Vertex(Vector3 position, Vector3 normal, Vector2 textureCoord) {
		this.position = position;
		this.normal = normal;
		this.textureCoord = textureCoord;
	}

	/**
	 * Create vertex from position and texture coordinate
	 * @param position Vertex position
	 * @param textureCoord Vertex texture coordinate
	 */
	public Vertex(Vector3 position, Vector2 textureCoord) {
		this.position = position;
		this.normal = Vector3.Zero();
		this.textureCoord = textureCoord;
	}

	/**
	 * Returns position of vertex
	 * @return Vertex position
	 */
	public Vector3 GetPosition() {
		return position;
	}

	/**
	 * Returns normal of vertex
	 * @return Vertex normal
	 */
	public Vector3 GetNormal() {
		return normal;
	}

	/**
	 * Returns texture coordinate of vertex
	 * @return Vertex texture coordinate
	 */
	public Vector2 GetTextureCoordinates() {
		return textureCoord;
	}

	/**
	 * Returns tangent of vertex
	 * @return Vertex tangent
	 */
	public Vector3 GetTangent() {
		return tangent;
	}

	/**
	 * Returns bitangent of vertex
	 * @return Vertex bitangent
	 */
	public Vector3 GetBitangent() {
		return bitangent;
	}

	/**
	 * Sets the position of the vertex
	 * @param position New position
	 */
	public void SetPosition(Vector3 position) {
		this.position = position;
	}

	/**
	 * Sets the normal of the vertex
	 * @param normal New normal
	 */
	public void SetNormal(Vector3 normal) {
		this.normal = normal;
	}

	/**
	 * Sets the texture coordinate of the vertex
	 * @param textureCoord New texture coordinate
	 */
	public void SetTextureCoordinate(Vector2 textureCoord) {
		this.textureCoord = textureCoord;
	}

	/**
	 * Sets the tangent of the vertex
	 * @param tangent New tangent
	 */
	public void SetTangent(Vector3 tangent) {
		this.tangent = tangent;
	}

	/**
	 * Sets the bitangent of the vertex
	 * @param bitangent New bitangent
	 */
	public void SetBitangent(Vector3 bitangent) {
		this.bitangent = bitangent;
	}

	public AIBone GetBone() {
		return bone;
	}

	public void SetBone(AIBone bone) {
		this.bone = bone;
	}

	public List<Float> GetWeights() {
		return weights;
	}

	public void AddWeight(Float weight) {
		this.weights.add(weight);
	}

}
