package Radium.Math;

/**
 * Generates random numbers
 */
public class Random {

	protected Random() {}

	/**
	 * Random int between ranges
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return Random number between ranges of min and max
	 */
	public static int RandomInt(int min, int max) {
		return (int) (Math.random() * max + min);
	}

	/**
	 * Random float between ranges
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return Random number between ranges of min and max
	 */
	public static float RandomFloat(float min, float max) {
		return (float) (Math.random() * max + min);
	}
}
