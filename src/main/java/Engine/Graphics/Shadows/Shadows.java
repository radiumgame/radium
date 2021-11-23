package Engine.Graphics.Shadows;

import Engine.Graphics.Framebuffer.DepthFramebuffer;
import Engine.Util.NonInstantiatable;

public final class Shadows extends NonInstantiatable {

    public static DepthFramebuffer framebuffer;
    public static int ShadowFramebufferSize = 2048;

    public static void Initialize() {
        framebuffer = new DepthFramebuffer(ShadowFramebufferSize, ShadowFramebufferSize);
    }

}
