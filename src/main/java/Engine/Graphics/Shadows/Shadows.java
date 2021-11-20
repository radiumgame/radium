package Engine.Graphics.Shadows;

import Engine.Util.NonInstantiatable;

public final class Shadows extends NonInstantiatable {

    public static DepthFramebuffer framebuffer;
    public static boolean ShadowFrame = false;
    public static int ShadowFramebufferSize = 2048;

    public static void Initialize() {
        framebuffer = new DepthFramebuffer(ShadowFramebufferSize, ShadowFramebufferSize);
    }

}
