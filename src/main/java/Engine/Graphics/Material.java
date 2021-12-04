package Engine.Graphics;

import Editor.Console;
import Engine.Util.FileUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import org.lwjgl.opengl.GL13;

import java.io.PrintWriter;

public class Material {
	
	public String path;
	public float reflectivity = 1f;
	public float shineDamper = 10f;
	public boolean cubeMapReflections = false;
	private transient Texture texture;
	public transient java.io.File file;
	public File materialFile;
	private transient float width, height;
	private transient int textureID = 0;
	
	public Material(String path) {
		this.path = path;

		CreateMaterial();
	}
	
	public void CreateMaterial() {
		try {
			texture = new Texture(path);
			width = texture.width;
			height = texture.height;
			textureID = texture.textureID;
			file = new java.io.File(path);
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

	public static void SaveMaterial(Material material, String path) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(material);
		File file = new File(path);

		try {
			PrintWriter pw = new PrintWriter(file);
			pw.flush();
			pw.close();

			FileUtility.Write(file, json);
		} catch (Exception e) {
			Console.Error(e);
		}
	}

	public static Material FromSource(String path) {
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			Material material = gson.fromJson(FileUtility.ReadFile(new File(path)), Material.class);
			material.CreateMaterial();
			material.materialFile = new File(path);

			return material;
		} catch (Exception e) {
			return FromSource("EngineAssets/Materials/Default.radiummat");
		}
	}

	public static Material Default() {
		return FromSource("EngineAssets/Materials/Default.radiummat");
	}

}
