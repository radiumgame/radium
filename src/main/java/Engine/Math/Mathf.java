package Engine.Math;

import Engine.Math.Vector.Vector2;
import Engine.Math.Vector.Vector3;
import Engine.Util.NonInstantiatable;
import org.joml.Vector3f;

public final class Mathf extends NonInstantiatable {
	
	public static float Square(float number) {
		return number * number;
	}

	public static float SquareRoot(float number) { return (float)Math.sqrt(number); }
	
	public static float Cube(float number) {
		return number * number * number;
	}
	
	public static float Power(float number, float power) {
		return (float)Math.pow(number, power);
	}

	public static int Round(float number) { return Math.round(number); }

	public static int Floor(float number) { return (int)Math.floor(number); }

	public static int Ceiling(float number) { return (int)Math.ceil(number); }

	public static float Sine(float number) { return (float)Math.sin(number); }

	public static float Asin(float number) { return (float)Math.asin(number); }

	public static float Cosine(float number) { return (float)Math.cos(number); }

	public static float Acos(float number) { return (float)Math.acos(number); }

	public static float Tangent(float number) { return (float)Math.tan(number); }

	public static float Atan(float number) { return (float)Math.atan(number); }

	public static float Atan2(Vector2 position) { return (float)Math.atan2(position.x, position.y); }

	public static float Radians(float number) { return (float)Math.toRadians(number); }

	public static float Degrees(float number) { return (float)Math.toDegrees(number); }

	public static float Absolute(float value) { return Math.abs(value); }

	public static Vector3 Cross(Vector3 value, Vector3 other) {
		Vector3f jomlValue = new Vector3f(value.x, value.y, value.z);
		Vector3f jomlOther = new Vector3f(other.x, other.y, other.z);

		Vector3f crossProduct = jomlValue.cross(jomlOther);
		return new Vector3(crossProduct.x, crossProduct.y, crossProduct.z);
	}

	public static final float PI = (float)Math.PI;

}
