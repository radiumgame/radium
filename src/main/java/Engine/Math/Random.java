package Engine.Math;

import Engine.Util.NonInstantiatable;

public final class Random extends NonInstantiatable {
	
	public static int RandomInt(int min, int max) {
		return (int) (Math.random() * max + min);
	}
	
	public static float RandomFloat(float min, float max) {
		return (float) (Math.random() * max + min);
	}
}
