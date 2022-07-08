package Radium.Engine.Graphics.Shadows;

import Radium.Engine.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Engine.Graphics.Shader.Shader;

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

    public static Shader performance = new Shader("EngineAssets/Shaders/Shadows/vert.glsl", "EngineAssets/Shaders/Shadows/frag.glsl");

    protected Shadows() {}

    /**
     * Creates shadows framebuffer
     */
    public static void CreateFramebuffer() {
        framebuffer = new DepthFramebuffer(ShadowFramebufferSize, ShadowFramebufferSize);
    }

}
