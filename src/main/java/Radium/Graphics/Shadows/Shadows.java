package Radium.Graphics.Shadows;

import Radium.Graphics.Framebuffer.DepthFramebuffer;

public class Shadows {

    public static DepthFramebuffer framebuffer;
    public static int ShadowFramebufferSize = 1024;

    protected Shadows() {}

    public static void CreateFramebuffer() {
        framebuffer = new DepthFramebuffer(ShadowFramebufferSize, ShadowFramebufferSize);
    }

}
