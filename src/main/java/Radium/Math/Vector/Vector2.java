package Radium.Math.Vector;

import Radium.Math.Mathf;

/**
 * Storing an X and Y value
 */
public class Vector2 {

	/**
	 * X value of the vector
	 */
	public float x;
	/**
	 * Y value of the vector
	 */
	public float y;

	/**
	 * Create Vector2 from 2 values
	 * @param x X
	 * @param y Y
	 */
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the X and Y values to different values
	 * @param x New X
	 * @param y New Y
	 */
	public void Set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns Vector2(0, 0)
	 * @return Vector2(0, 0)
	 */
	public static Vector2 Zero() { return new Vector2(0, 0); }

	/**
	 * Returns Vector2(1, 1)
	 * @return Vector2(1, 1)
	 */
	public static Vector2 One() { return new Vector2(1, 1); }

	/**
	 * Adds to vector2
	 * @param vector1
	 * @param vector2
	 * @return Vector 1 + Vector 2
	 */
	public static Vector2 Add(Vector2 vector1, Vector2 vector2) {
		return new Vector2(vector1.x + vector2.x, vector1.y + vector2.y);
	}

	/**
	 * Subtracts to vector2
	 * @param vector1
	 * @param vector2
	 * @return Vector 1 - Vector 2
	 */
	public static Vector2 Subtract(Vector2 vector1, Vector2 vector2) {
		return new Vector2(vector1.x - vector2.x, vector1.y - vector2.y);
	}

	/**
	 * Multiplies to vector2
	 * @param vector1
	 * @param vector2
	 * @return Vector 1 * Vector 2
	 */
	public static Vector2 Multiply(Vector2 vector1, Vector2 vector2) {
		return new Vector2(vector1.x * vector2.x, vector1.y * vector2.y);
	}

	/**
	 * Divides to vector2
	 * @param vector1
	 * @param vector2
	 * @return Vector 1 / Vector 2
	 */
	public static Vector2 Divide(Vector2 vector1, Vector2 vector2) {
		return new Vector2(vector1.x / vector2.x, vector1.y / vector2.y);
	}

	/**
	 * Returns the magnitude of the Vector3
	 * @param vector
	 * @return Magnitude of vector
	 */
	public static float Length(Vector2 vector) {
		return (float)Math.sqrt(vector.x * vector.x + vector.y * vector.y);
	}

	/**
	 * Returns normalized version of vector
	 * @param vector
	 * @return Normalized vector
	 */
	public static Vector2 Normalized(Vector2 vector) {
		float len= Vector2.Length(vector);
		return Vector2.Divide(vector, new Vector2(len, len));
	}

	/**
	 * Returns dot product of 2 vectors
	 * @param vector1
	 * @param vector2
	 * @return Dot(vector1, vector2);
	 */
	public static float Dot(Vector2 vector1, Vector2 vector2) {
		return vector1.x * vector2.x + vector1.y * vector2.y;
	}

	/**
	 * Returns distance between 2 vector2
	 * @param one
	 * @param two
	 * @return Sqrt(Pow(vec2.x - vec1.x, 2) + Pow(vec2.y - vec1.y, 2))
	 */
	public float Distance(Vector2 one, Vector2 two) {
		float formula = (Mathf.Square(two.x - one.x) + Mathf.Square(two.y - one.y));
		float distance = Mathf.Power(formula, 0.5f);
		return distance;
	}

	/**
	 * Unique hash code of vector
	 * @return Hash code
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	/**
	 * Returns if two vectors are equal
	 * @param obj
	 * @return this == vec
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2 other = (Vector2) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}
}
