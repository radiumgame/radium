package Radium.Engine.Graphics.Shadows;

import Radium.Engine.Graphics.Framebuffer.DepthFramebuffer;

/**
 * Shadow settings
 */
public class Shadows {

    /**
     * Shadow framebuffer
     */
    public static DepthFramebuffer framebuffer;
    /**
     * Shadow quality
     */
    public static int ShadowFramebufferSize = 1024;

    protected Shadows() {}

    /**
     * Creates shadows framebuffer
     */
    public static void CreateFramebuffer() {
        framebuffer = new DepthFramebuffer(ShadowFramebufferSize, ShadowFramebufferSize);
    }

}
