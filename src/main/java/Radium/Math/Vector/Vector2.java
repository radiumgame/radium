package Radium.Math.Vector;

import Radium.Math.Mathf;

public class Vector2 {

	public float x, y;
	
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void Set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static Vector2 Zero = new Vector2(0, 0);
	public static Vector2 One = new Vector2(1, 1);
	
	public static Vector2 Add(Vector2 vector1, Vector2 vector2) {
		return new Vector2(vector1.x + vector2.x, vector1.y + vector2.y);
	}
	
	public static Vector2 Subtract(Vector2 vector1, Vector2 vector2) {
		return new Vector2(vector1.x - vector2.x, vector1.y - vector2.y);
	}
	
	public static Vector2 Multiply(Vector2 vector1, Vector2 vector2) {
		return new Vector2(vector1.x * vector2.x, vector1.y * vector2.y);
	}
	
	public static Vector2 Divide(Vector2 vector1, Vector2 vector2) {
		return new Vector2(vector1.x / vector2.x, vector1.y / vector2.y);
	}
	
	public static float Length(Vector2 vector) {
		return (float)Math.sqrt(vector.x * vector.x + vector.y * vector.y);
	}
	
	public static Vector2 Normalized(Vector2 vector) {
		float len= Vector2.Length(vector);
		return Vector2.Divide(vector, new Vector2(len, len));
	}
	
	public static float Dot(Vector2 vector1, Vector2 vector2) {
		return vector1.x * vector2.x + vector1.y * vector2.y;
	}
	
	public float Distance(Vector2 one, Vector2 two) {
		float formula = (Mathf.Square(two.x - one.x) + Mathf.Square(two.y - one.y));
		float distance = Mathf.Power(formula, 0.5f);
		return distance;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
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
		Vector2 other = (Vector2) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}
}
