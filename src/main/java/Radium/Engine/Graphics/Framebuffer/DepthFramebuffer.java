package Radium.Engine.Graphics.Framebuffer;

import org.lwjgl.opengl.GL30C;

/**
 * Framebuffer that contains depth component
 */
public class DepthFramebuffer {

    /**
     * Determines if it is a depth testing frame
     */
    public static boolean DepthTesting = false;

    private int fboID, depthMap;

    /**
     * Creates depth framebuffer with predefined resolution
     * @param width Width of framebuffer
     * @param height Height of framebuffer
     */
    public DepthFramebuffer(int width, int height) {
        GenerateFramebuffer(width, height);
    }

    /**
     * Binds the framebuffer to the current frame
     */
    public void Bind() {
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, fboID);
    }

    /**
     * Unbinds the framebuffer
     */
    public void Unbind() {
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, 0);
    }

    private void GenerateFramebuffer(int width, int height) {
        fboID = GL30C.glGenFramebuffers();
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, fboID);

        depthMap = GL30C.glGenTextures();
        GL30C.glBindTexture(GL30C.GL_TEXTURE_2D, depthMap);
        GL30C.glTexImage2D(GL30C.GL_TEXTURE_2D, 0, GL30C.GL_DEPTH_COMPONENT, width, height, 0, GL30C.GL_DEPTH_COMPONENT, GL30C.GL_FLOAT, (float[])null);
        GL30C.glTexParameteri(GL30C.GL_TEXTURE_2D, GL30C.GL_TEXTURE_MIN_FILTER, GL30C.GL_NEAREST);
        GL30C.glTexParameteri(GL30C.GL_TEXTURE_2D, GL30C.GL_TEXTURE_MAG_FILTER, GL30C.GL_NEAREST);
        GL30C.glTexParameteri(GL30C.GL_TEXTURE_2D, GL30C.GL_TEXTURE_WRAP_S, GL30C.GL_CLAMP_TO_BORDER);
        GL30C.glTexParameteri(GL30C.GL_TEXTURE_2D, GL30C.GL_TEXTURE_WRAP_T, GL30C.GL_CLAMP_TO_BORDER);
        float[] borderColor = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        GL30C.glTexParameterfv(GL30C.GL_TEXTURE_2D, GL30C.GL_TEXTURE_BORDER_COLOR, borderColor);

        GL30C.glFramebufferTexture2D(GL30C.GL_FRAMEBUFFER, GL30C.GL_DEPTH_ATTACHMENT, GL30C.GL_TEXTURE_2D, depthMap, 0);
        GL30C.glDrawBuffer(GL30C.GL_NONE);
        GL30C.glReadBuffer(GL30C.GL_NONE);
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, 0);
    }

    /**
     * Returns the FBO of the framebuffer
     * @return Framebuffer FBO
     */
    public int GetFBO() {
        return fboID;
    }

    /**
     * Returns texture of framebuffer
     * @return Texture of framebuffer
     */
    public int GetDepthMap() { return depthMap; }

}
