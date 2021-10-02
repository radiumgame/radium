package Engine.Util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class BufferTools extends NonInstantiatable {
	public static FloatBuffer AsFlippedFloatBuffer(float[] values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }
}
