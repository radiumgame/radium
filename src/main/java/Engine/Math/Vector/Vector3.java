package Engine.Math.Vector;

import Engine.Math.Axis;
import Engine.Math.Mathf;

public class Vector3 {
	public float x, y, z;
	
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void Set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Vector3 Zero = new Vector3(0, 0, 0);
	public static Vector3 One = new Vector3(1, 1, 1);
	
	public static Vector3 Add(Vector3 vector1, Vector3 vector2) {
		return new Vector3(vector1.x + vector2.x, vector1.y + vector2.y, vector1.z + vector2.z);
	}
	
	public static Vector3 Subtract(Vector3 vector1, Vector3 vector2) {
		return new Vector3(vector1.x - vector2.x, vector1.y - vector2.y, vector1.z - vector2.z);
	}
	
	public static Vector3 Multiply(Vector3 vector1, Vector3 vector2) {
		return new Vector3(vector1.x * vector2.x, vector1.y * vector2.y, vector1.z * vector2.z);
	}
	
	public static Vector3 Divide(Vector3 vector1, Vector3 vector2) {
		return new Vector3(vector1.x / vector2.x, vector1.y / vector2.y, vector1.z / vector2.z);
	}
	
	public static float Length(Vector3 vector) {
		return (float)Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
	}
	
	public static Vector3 Normalized(Vector3 vector) {
		float len = Vector3.Length(vector);
		return Vector3.Divide(vector, new Vector3(len, len, len));
	}
	
	public static float Dot(Vector3 vector1, Vector3 vector2) {
		return vector1.x * vector2.x + vector1.y * vector2.y + vector1.z * vector2.z;
	}
	
	public static float Distance(Vector3 one, Vector3 two) {
		float formula = (Mathf.Square(two.x - one.x) + Mathf.Square(two.y - one.y) + Mathf.Square(two.z - one.z));
		float distance = Mathf.Power(formula, 0.5f);
		return distance;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	@Override
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

	@Override
	public String toString() {
		return "{ " + x + ", " + y + ", " + z + " }";
	}
}