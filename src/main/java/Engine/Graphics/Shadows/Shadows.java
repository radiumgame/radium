package Engine.Graphics.Shadows;

import Engine.Graphics.Framebuffer;
import Engine.Util.NonInstantiatable;

public final class Shadows extends NonInstantiatable {

    public static ShadowFrameBuffer framebuffer;
    public static boolean ShadowFrame = false;
    public static int ShadowFramebufferSize = 2048;

    public static void Initialize() {
        framebuffer = new ShadowFrameBuffer(ShadowFramebufferSize, ShadowFramebufferSize);
    }

}
