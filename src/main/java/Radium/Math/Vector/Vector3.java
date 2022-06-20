package Radium.Math.Vector;

import Radium.Math.Axis;
import Radium.Math.Mathf;
import org.joml.Vector3f;

/**
 * Storing an X, Y, and Z value
 */
public class Vector3 implements Cloneable {

	/**
	 * X value of vector
	 */
	public float x;
	/**
	 * Y value of vector
	 */
	public float y;
	/**
	 * Z value of vector
	 */
	public float z;

	/**
	 * Create vector from 3 values
	 * @param x X value
	 * @param y Y value
	 * @param z Z value
	 */
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sets the X, Y, and Z values to a new value
	 * @param x New X
	 * @param y New Y
	 * @param z New Z
	 */
	public void Set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void Set(Vector3 vector) {
		this.x = vector.x;
		this.y = vector.y;
		this.z = vector.z;
	}

	/**
	 * @return Vector3(0, 0, 0)
	 */
	public static Vector3 Zero() { return new Vector3(0, 0, 0); }

	/**
	 * @return Vector3(1, 1, 1)
	 */
	public static Vector3 One() { return new Vector3(1, 1, 1); }

	/**
	 * Adds 2 vectors
	 * @param vector1
	 * @param vector2
	 * @return vector1 + vector2
	 */
	public static Vector3 Add(Vector3 vector1, Vector3 vector2) {
		return new Vector3(vector1.x + vector2.x, vector1.y + vector2.y, vector1.z + vector2.z);
	}

	/**
	 * Subtracts 2 vectors
	 * @param vector1
	 * @param vector2
	 * @return vector1 - vector2
	 */
	public static Vector3 Subtract(Vector3 vector1, Vector3 vector2) {
		return new Vector3(vector1.x - vector2.x, vector1.y - vector2.y, vector1.z - vector2.z);
	}

	/**
	 * Multiplies 2 vectors
	 * @param vector1
	 * @param vector2
	 * @return vector1 * vector2
	 */
	public static Vector3 Multiply(Vector3 vector1, Vector3 vector2) {
		return new Vector3(vector1.x * vector2.x, vector1.y * vector2.y, vector1.z * vector2.z);
	}

	/**
	 * Divides 2 vectors
	 * @param vector1
	 * @param vector2
	 * @return vector1 / vector2
	 */
	public static Vector3 Divide(Vector3 vector1, Vector3 vector2) {
		return new Vector3(vector1.x / vector2.x, vector1.y / vector2.y, vector1.z / vector2.z);
	}

	/**
	 * @param vector
	 * @return Magnitude of the vector
	 */
	public static float Length(Vector3 vector) {
		return Mathf.SquareRoot(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
	}

	/**
	 * Returns the normalized version of the vector
	 * @param vector
	 * @return Normalized vector
	 */
	public static Vector3 Normalized(Vector3 vector) {
		float len = Vector3.Length(vector);
		return Vector3.Divide(vector, new Vector3(len, len, len));
	}

	/**
	 * Lerps to vectors
	 */
	public static Vector3 Lerp(Vector3 one, Vector3 two, float time) {
		Vector3f onef = new Vector3f(one.x, one.y, one.z);
		Vector3f twof = new Vector3f(two.x, two.y, two.z);
		Vector3f lerpf = onef.lerp(twof, time);
		return new Vector3(lerpf.x, lerpf.y, lerpf.z);
	}

	/**
	 * Dot product of 2 vectors
	 * @param vector1
	 * @param vector2
	 * @return Dot(vector1, vector2);
	 */
	public static float Dot(Vector3 vector1, Vector3 vector2) {
		return vector1.x * vector2.x + vector1.y * vector2.y + vector1.z * vector2.z;
	}

	/**
	 * Distance between 2 vectors
	 * @param one
	 * @param two
	 * @return Sqrt(Pow(vec2.x - vec1.x, 2) + Pow(vec2.y - vec1.y, 2) + Pow(vec2.z - vec1.z, 2))
	 */
	public static float Distance(Vector3 one, Vector3 two) {
		float formula = (Mathf.Square(two.x - one.x) + Mathf.Square(two.y - one.y) + Mathf.Square(two.z - one.z));
		return Mathf.Power(formula, 0.5f);
	}

	/**
	 * Returns cross product of 2 vectors
	 * @param one
	 * @param two
	 * @return Cross(vec1, vec2);
	 */
	public static Vector3 Cross(Vector3 one, Vector3 two) {
		Vector3f v1 = new Vector3f(one.x, one.y, one.z);
		Vector3f v2 = new Vector3f(two.x, two.y, two.z);
		Vector3f cross = v1.cross(v2);

		return new Vector3(cross.x, cross.y, cross.z);
	}

	/**
	 * Clamps one of the values between a certain range
	 * @param axis Clamped axis
	 * @param min Minimum value
	 * @param max Maximum value
	 */
	public void Clamp(Axis axis, float min, float max) {
		if (axis == Axis.X) {
			if (x <= min) {
				x = min;
			} else if (x >= max) {
				x = max;
			}
		}
		if (axis == Axis.Y) {
			if (y <= min) {
				y = min;
			} else if (y >= max) {
				y = max;
			}
		}
		if (axis == Axis.Z) {
			if (z <= min) {
				z = min;
			} else if (z >= max) {
				z = max;
			}
		}
	}

	/**
	 * Unique hash code of vector
	 * @return Hash code
	 */
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	/**
	 * Returns if two vectors are equal
	 * @param obj
	 * @return this == vec
	 */
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector3 other = (Vector3) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}

	/**
	 * @return { x, y, z }
	 */
	
	public String toString() {
		return "{ " + x + ", " + y + ", " + z + " }";
	}

	@Override
	public Vector3 clone() {
		return new Vector3(x, y, z);
	}
}