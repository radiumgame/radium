package Radium.Graphics;

import Radium.Color.Color;
import Radium.Graphics.Lighting.LightCalculationMode;
import RadiumEditor.Console;

import java.io.File;
import org.lwjgl.opengl.GL13;

/**
 * Object rendering settings
 */
public class Material {

	/**
	 * Texture path
	 */
	public String path;
	/**
	 * Normal map texture path
	 */
	public String normalMapPath;
	/**
	 * Specular/AO map texture path
	 */
	public String specularMapPath;

	/**
	 * Determines whether to use specular lighting
	 */
	public boolean specularLighting = true;
	/**
	 * Determines whether to use normal map
	 */
	public boolean useNormalMap = false;
	/**
	 * Determines whether to use specular map
	 */
	public boolean useSpecularMap = false;

	/**
	 * Reflectivity of object
	 */
	public float reflectivity = 1f;
	/**
	 * Size of specular highlight
	 */
	public float shineDamper = 10f;
	/**
	 * Color of material
	 */
	public Color color = new Color(255, 255, 255, 255);

	public float metallic = 4;
	public float fresnel = 5;
	public float glossiness = 0.04f;

	public LightCalculationMode lightCalculationMode = LightCalculationMode.Normal;

	private transient Texture texture;
	private transient Texture normalTexture;
	private transient Texture specularTexture;

	/**
	 * Texture file
	 */
	public transient java.io.File file;
	/**
	 * Normal map file
	 */
	public transient java.io.File normalFile;
	/**
	 * Specular/AO map file
	 */
	public transient java.io.File specularFile;

	private transient float width, height;

	private transient int textureID = 0;
	private transient int normalMapID;
	private transient int specularMapID;

	/**
	 * Create material with texture path
	 * @param path Texture path
	 */
	public Material(String path) {
		this.path = path;

		CreateMaterial();
	}

	/**
	 * Create the materials texture id's
	 */
	public void CreateMaterial() {
		try {
			if (normalMapPath == null) {
				normalMapPath = "EngineAssets/Textures/Misc/blank.jpg";
			}
			if (specularMapPath == null) {
				specularMapPath = "EngineAssets/Textures/Misc/blank.jpg";
			}

			texture = new Texture(path);
			normalTexture = new Texture(normalMapPath);
			specularTexture = new Texture(specularMapPath);

			width = texture.width;
			height = texture.height;

			textureID = texture.textureID;
			normalMapID = normalTexture.textureID;
			specularMapID = specularTexture.textureID;

			file = new java.io.File("./" + path);
			normalFile = new File(normalMapPath);
			specularFile = new File(specularMapPath);
		}
		catch (Exception e) {
			Console.Error(e);
		}
	}

	/**
	 * Destroy the materials buffers
	 */
	public void DestroyMaterial() {
		GL13.glDeleteTextures(textureID);
	}

	/**
	 * Returns width of texture
	 * @return Texture width
	 */
	public float GetWidth() {
		return width;
	}

	/**
	 * Returns height of texture
	 * @return Texture height
	 */
	public float GetHeight() {
		return height;
	}

	/**
	 * Returns texture id of main texture
	 * @return Texture ID
	 */
	public int GetTextureID() {
		return textureID;
	}

	/**
	 * Returns texture id of normal map
	 * @return Normal map ID
	 */
	public int GetNormalTextureID() { return normalMapID; }

	/**
	 * Returns texture id of specular/AO map
	 * @return Specular/AO map ID
	 */
	public int GetSpecularMapID() { return specularMapID; }

	public static Material Clone(Material material) {
		Material mat = new Material(material.path);
		mat.normalMapPath = material.normalMapPath;
		mat.specularMapPath = material.specularMapPath;
		mat.useNormalMap = material.useNormalMap;
		mat.useSpecularMap = material.useSpecularMap;
		mat.specularLighting = material.specularLighting;
		mat.reflectivity = material.reflectivity;
		mat.shineDamper = material.shineDamper;
		mat.color = material.color;
		mat.CreateMaterial();

		return mat;
	}

}
