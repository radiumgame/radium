package Radium.Graphics;

import Radium.Color;
import RadiumEditor.Console;
import Radium.Util.FileUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import org.lwjgl.opengl.GL13;

import java.io.PrintWriter;

public class Material {
	
	public String path;
	public String normalMapPath;

	public boolean useNormalMap = false;
	public float reflectivity = 1f;
	public float shineDamper = 10f;
	public Color color = new Color(255, 255, 255, 255);

	private transient Texture texture;
	private transient Texture normalTexture;

	public transient java.io.File file;
	public transient java.io.File normalFile;

	private transient float width, height;

	private transient int textureID = 0;
	private transient int normalMapID;
	
	public Material(String path) {
		this.path = path;

		CreateMaterial();
	}
	
	public void CreateMaterial() {
		try {
			if (normalMapPath == null) {
				normalMapPath = "EngineAssets/Textures/Misc/blank.jpg";
			}

			texture = new Texture(path);
			normalTexture = new Texture(normalMapPath);

			width = texture.width;
			height = texture.height;

			textureID = texture.textureID;
			normalMapID = normalTexture.textureID;

			file = new java.io.File("./" + path);
			normalFile = new File(normalMapPath);
		}
		catch (Exception e) {
			Console.Error(e);
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

	public int GetNormalTextureID() { return normalMapID; }

}
