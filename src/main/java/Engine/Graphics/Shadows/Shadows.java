package Engine.Graphics.Shadows;

import Engine.Graphics.Framebuffer;
import Engine.Util.NonInstantiatable;

public final class Shadows extends NonInstantiatable {

    public static ShadowFrameBuffer framebuffer;
    public static boolean ShadowFrame = false;

    public static void Initialize() {
        framebuffer = new ShadowFrameBuffer(1024, 1024);
    }

}
