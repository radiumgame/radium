package Engine.Graphics.Shadows;

import Engine.Graphics.Framebuffer.DepthFramebuffer;
import Engine.Util.NonInstantiatable;

public final class Shadows extends NonInstantiatable {

    public static DepthFramebuffer framebuffer;
    public static int ShadowFramebufferSize = 1024;

    public static void CreateFramebuffer() {
        framebuffer = new DepthFramebuffer(ShadowFramebufferSize, ShadowFramebufferSize);
    }

}
