package Radium;

import Radium.Math.Vector.Vector3;

public class Color {
	
	public float r, g, b, a;
	
	public Color(int r, int g, int b) {
		this.r = r / 255;
		this.g = g / 255;
		this.b = b / 255;
		this.a = 1;
	}

	public Color(int r, int g, int b, int a) {
		this.r = r / 255;
		this.g = g / 255;
		this.b = b / 255;
		this.a = a / 255;
	}

	public Color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1;
	}

	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void Set(int r, int g, int b) {
		this.r = r / 255f;
		this.g = g / 255f;
		this.b = b / 255f;
	}

	public void Set(int r, int g, int b, int a) {
		this.r = r / 255f;
		this.g = g / 255f;
		this.b = b / 255f;
		this.a = a / 255f;
	}

	public void Set(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void Set(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public static Color FromVector3(Vector3 vector) {
		return new Color(vector.x, vector.y, vector.z);
	}

	public Vector3 ToVector3() {
		return new Vector3(r, g, b);
	}
	
	public static Color Red() {
		return new Color(255, 0, 0);
	}
	
	public static Color Orange() {
		return new Color(255, 128, 0);
	}
	
	public static Color Yellow() {
		return new Color(255, 255, 0);
	}
	
	public static Color Green() {
		return new Color(0, 255, 0);
	}
	
	public static Color Cyan() {
		return new Color(0, 255, 255);
	}
	
	public static Color Blue() {
		return new Color(0, 0, 255);
	}
	
	public static Color Purple() {
		return new Color(127, 0, 255);
	}
	
	public static Color Pink() {
		return new Color(255, 0, 255);
	}
}
