package Radium.Math;

import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import org.joml.Vector3f;

/**
 * Lots of math functions
 */
public class Mathf {

	protected Mathf() {}

	/**
	 * Returns value squared
	 * @param number Value
	 * @return Value squared (number * number)
	 */
	public static float Square(float number) {
		return number * number;
	}

	/**
	 * Returns square root of value
	 * @param number Value
	 * @return Square root of number
	 */
	public static float SquareRoot(float number) { return (float)Math.sqrt(number); }

	/**
	 * Returns value cubed
	 * @param number Value
	 * @return Value cubed (number * number * number)
	 */
	public static float Cube(float number) {
		return number * number * number;
	}

	/**
	 * Returns value to the power of another value
	 * @param number Value
	 * @param power Power
	 * @return Value to the power of power (value^power)
	 */
	public static float Power(float number, float power) {
		return (float)Math.pow(number, power);
	}

	/**
	 * Rounds the value to the nearest whole number
	 * @param number Value
	 * @return Value rounded to the closest integer
	 */
	public static int Round(float number) { return Math.round(number); }

	/**
	 * Rounds the value down
	 * @param number Value
	 * @return Value rounded down
	 */
	public static int Floor(float number) { return (int)Math.floor(number); }

	/**
	 * Rounds the value up
	 * @param number Value
	 * @return Value roudned up
	 */
	public static int Ceiling(float number) { return (int)Math.ceil(number); }

	/**
	 * Trigonometric sine of a value
	 * @param number Value
	 * @return Sine of value
	 */
	public static float Sine(float number) { return (float)Math.sin(number); }

	/**
	 * Returns the arc sine of value
	 * @param number Value
	 * @return Arc sine of value
	 */
	public static float Asin(float number) { return (float)Math.asin(number); }

	/**
	 * Trigonometric cosine of a value
	 * @param number Value
	 * @return Cosine of value
	 */
	public static float Cosine(float number) { return (float)Math.cos(number); }

	/**
	 * Returns the arc cosine of value
	 * @param number Value
	 * @return Arc cosine of value
	 */
	public static float Acos(float number) { return (float)Math.acos(number); }

	/**
	 * Trigonometric tangent of a value
	 * @param number Value
	 * @return Tangent of value
	 */
	public static float Tangent(float number) { return (float)Math.tan(number); }

	/**
	 * Returns the arc tangent of value
	 * @param number Value
	 * @return Arc tangent of value
	 */
	public static float Atan(float number) { return (float)Math.atan(number); }

	/**
	 * Returns arc of tangent between -PI and PI
	 * @param position Value
	 * @return Atan2 of value
	 */
	public static float Atan2(Vector2 position) { return (float)Math.atan2(position.x, position.y); }

	/**
	 * Converts degrees to radians
	 * @param number Value
	 * @return Value to radians
	 */
	public static float Radians(float number) { return (float)Math.toRadians(number); }

	/**
	 * Converts radians to degrees
	 * @param number Value
	 * @return Value to degrees
	 */
	public static float Degrees(float number) { return (float)Math.toDegrees(number); }

	/**
	 * Absolute value of a value
	 * @param value Value
	 * @return Absolute value of value
	 */
	public static float Absolute(float value) { return Math.abs(value); }

	/**
	 * Returns cross product of 2 vectors
	 * @param value Vector 1
	 * @param other Vector 2
	 * @return Cross of Vector 1 and Vector 2
	 */
	public static Vector3 Cross(Vector3 value, Vector3 other) {
		Vector3f jomlValue = new Vector3f(value.x, value.y, value.z);
		Vector3f jomlOther = new Vector3f(other.x, other.y, other.z);

		Vector3f crossProduct = jomlValue.cross(jomlOther);
		return new Vector3(crossProduct.x, crossProduct.y, crossProduct.z);
	}

	/**
	 * Value of PI
	 */
	public static final float PI = (float)Math.PI;

}
