package Engine.Math;

public class Mathf {
	
	public static float Square(float number) {
		return number * number;
	}
	
	public static float Cube(float number) {
		return number * number * number;
	}
	
	public static float Power(float number, float power) {
		return (float)Math.pow(number, power);
	}

	public static int Round(float number) { return Math.round(number); }
}
