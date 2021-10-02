package Engine.Graphics;

import org.lwjgl.opengl.GL13;

public class Material {
	
	private String path;
	public transient Texture texture;
	public transient java.io.File file;
	private transient float width, height;
	private transient int textureID;
	
	public Material(String path) {
		this.path = path;
	}
	
	public void CreateMaterial() {
		try {
			try {
				texture = new Texture(path);
				width = texture.width;
				height = texture.height;
				textureID = texture.textureID;
				file = new java.io.File(path);
			}
			catch (Exception e) {
				System.out.println("Error creating material.");
			}
		}
		catch (NullPointerException e) {
			System.out.println("Couldn't find path: " + path);
		}
	}
	
	public void DestroyMaterial() {
		GL13.glDeleteTextures(textureID);
	}

	public float GetWidth() {
		return width;
	}

	public float GetHeight() {
		return height;
	}

	public int GetTextureID() {
		return textureID;
	}
}
