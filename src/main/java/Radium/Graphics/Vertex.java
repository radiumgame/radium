package Radium.Graphics;

import Radium.Math.Vector.*;

public class Vertex {
	private Vector3 position, normal, tangent = Vector3.Zero, bitangent = Vector3.Zero;
	private Vector2 textureCoord;

	public Vertex(Vector3 position, Vector3 normal, Vector2 textureCoord) {
		this.position = position;
		this.normal = normal;
		this.textureCoord = textureCoord;
	}
	
	public Vertex(Vector3 position, Vector2 textureCoord) {
		this.position = position;
		this.normal = Vector3.Zero;
		this.textureCoord = textureCoord;
	}

	public Vector3 GetPosition() {
		return position;
	}
	
	public Vector3 GetNormal() {
		return normal;
	}
	
	public Vector2 GetTextureCoordinates() {
		return textureCoord;
	}

	public Vector3 GetTangent() {
		return tangent;
	}

	public Vector3 GetBitangent() {
		return bitangent;
	}

	public void SetPosition(Vector3 position) {
		this.position = position;
	}

	public void SetNormal(Vector3 normal) {
		this.normal = normal;
	}

	public void SetTextureCoordinate(Vector2 textureCoord) {
		this.textureCoord = textureCoord;
	}

	public void SetTangent(Vector3 tangent) {
		this.tangent = tangent;
	}

	public void SetBitangent(Vector3 bitangent) {
		this.bitangent = bitangent;
	}
}
