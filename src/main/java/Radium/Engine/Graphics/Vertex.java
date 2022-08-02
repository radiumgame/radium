package Radium.Engine.Graphics;

import Radium.Engine.Math.Vector.*;
import org.lwjgl.assimp.AIBone;

import java.util.ArrayList;
import java.util.List;

/**
 * A mesh vertex
 */
public class Vertex {

	private Vector3 position;
	private Vector3 normal;
	private Vector2 textureCoord;

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

}
