package Engine.Graphics;

import Engine.Math.Vector.*;

public class Vertex {
	private Vector3 position, normal;
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
}
