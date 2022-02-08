package Radium;

import Radium.Math.Vector.Vector3;

/**
 * Color class used for creating and defining colors
 */
public class Color {

	/**
	 * Color red value
	 */
	public float r;
	/**
	 * Color green value
	 */
	public float g;
	/**
	 * Color blue value
	 */
	public float b;
	/**
	 * Color alpha value
	 */
	public float a;

	/**
	 * Create color from 0-255 values
	 */
	public Color(int r, int g, int b) {
		this.r = r / 255;
		this.g = g / 255;
		this.b = b / 255;
		this.a = 1;
	}

	/**
	 * Create color from 0-255 values
	 */
	public Color(int r, int g, int b, int a) {
		this.r = r / 255;
		this.g = g / 255;
		this.b = b / 255;
		this.a = a / 255;
	}

	/**
	 * Create color from 0-1 values
	 */
	public Color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1;
	}

	/**
	 * Create color from 0-1 values
	 */
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/**
	 * Set color values from 0-255
	 */
	public void Set(int r, int g, int b) {
		this.r = r / 255f;
		this.g = g / 255f;
		this.b = b / 255f;
	}

	/**
	 * Set color values from 0-255
	 */
	public void Set(int r, int g, int b, int a) {
		this.r = r / 255f;
		this.g = g / 255f;
		this.b = b / 255f;
		this.a = a / 255f;
	}

	/**
	 * Set color values from 0-1
	 */
	public void Set(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 * Set color values from 0-1
	 */
	public void Set(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/**
	 * Create a color from a {@link Vector3 vector3}
	 * @param vector
	 * @return Color(x, y, z)
	 */
	public static Color FromVector3(Vector3 vector) {
		return new Color(vector.x, vector.y, vector.z);
	}

	/**
	 * Convert color to a {@link Vector3 value}
	 * @return Vector3(r, g, b)
	 */
	public Vector3 ToVector3() {
		return new Vector3(r, g, b);
	}

	/**
	 * @return Color(255, 0, 0)
	 */
	public static Color Red() {
		return new Color(255, 0, 0);
	}

	/**
	 * @return Color(255, 128, 0)
	 */
	public static Color Orange() {
		return new Color(255, 128, 0);
	}

	/**
	 * @return Color(255, 255, 0)
	 */
	public static Color Yellow() {
		return new Color(255, 255, 0);
	}

	/**
	 * @return Color(0, 255, 0)
	 */
	public static Color Green() {
		return new Color(0, 255, 0);
	}

	/**
	 * @return Color(0, 255, 255)
	 */
	public static Color Cyan() {
		return new Color(0, 255, 255);
	}

	/**
	 * @return Color(0, 0, 255)
	 */
	public static Color Blue() {
		return new Color(0, 0, 255);
	}

	/**
	 * @return Color(128, 0, 255)
	 */
	public static Color Purple() {
		return new Color(128, 0, 255);
	}

	/**
	 * @return Color(255, 0, 255)
	 */
	public static Color Pink() {
		return new Color(255, 0, 255);
	}
}
