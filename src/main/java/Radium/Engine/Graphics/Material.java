package Radium.Engine.Graphics;

import Radium.Engine.Color.Color;
import Radium.Engine.Graphics.Lighting.LightCalculationMode;
import Radium.Editor.Console;

import java.io.File;

import Radium.Engine.Graphics.Lighting.Lighting;
import org.lwjgl.opengl.GL13;

/**
 * Object rendering settings
 */
public class Material {

	public String path;
	public String normalMapPath;
	public String specularMapPath;
	public String displacementMapPath;

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
	public boolean useDisplacementMap = false;

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

	public float metallic = 2;
	public float fresnel = 5;
	public float glossiness = 0.02f;

	public LightCalculationMode lightCalculationMode = LightCalculationMode.Normal;

	public transient Texture texture;
	public transient Texture normalTexture;
	public transient Texture specularTexture;
	public transient Texture displacementTexture;

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
	public transient java.io.File displacementFile;

	private transient float width, height;

	private transient int textureID = 0;

	public Material() {

	}

	/**
	 * Create material with texture path
	 * @param path Texture path
	 */
	public Material(String path) {
		this.path = path;

		CreateMaterial();
		if (Lighting.DefaultPBR) {
			lightCalculationMode = LightCalculationMode.PBR;
		}
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
			if (displacementMapPath == null) {
				displacementMapPath = "EngineAssets/Textures/Misc/blank.jpg";
			}

			texture = new Texture(path);
			normalTexture = new Texture(normalMapPath);
			specularTexture = new Texture(specularMapPath);
			displacementTexture = new Texture(displacementMapPath);

			width = texture.width;
			height = texture.height;

			file = new java.io.File("./" + path);
			normalFile = new File(normalMapPath);
			specularFile = new File(specularMapPath);
			displacementFile = new File(displacementMapPath);
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
		return texture.GetTextureID();
	}

	/**
	 * Returns texture id of normal map
	 * @return Normal map ID
	 */
	public int GetNormalTextureID() { return normalTexture.GetTextureID(); }

	/**
	 * Returns texture id of specular/AO map
	 * @return Specular/AO map ID
	 */
	public int GetSpecularMapID() { return specularTexture.GetTextureID(); }

	public int GetDisplacementMapID() { return displacementTexture.GetTextureID(); }

	public static Material Clone(Material material) {
		Material mat = new Material(material.path);
		mat.normalMapPath = material.normalMapPath;
		mat.specularMapPath = material.specularMapPath;
		mat.displacementMapPath = material.displacementMapPath;
		mat.useNormalMap = material.useNormalMap;
		mat.useSpecularMap = material.useSpecularMap;
		mat.useDisplacementMap = material.useDisplacementMap;
		mat.specularLighting = material.specularLighting;
		mat.reflectivity = material.reflectivity;
		mat.shineDamper = material.shineDamper;
		mat.color = material.color;
		mat.CreateMaterial();

		return mat;
	}

}
