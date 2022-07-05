package Radium.Engine.Graphics.Framebuffer;

import org.lwjgl.opengl.GL30C;

/**
 * A framebuffer with color component
 */
public class Framebuffer {

    private int fboID;
    private FrameBufferTexture texture;

    /**
     * Create framebuffer with predefined resolution
     * @param width Width of framebuffer
     * @param height
     */
    public Framebuffer(int width, int height) {
        GenerateFramebuffer(width, height);
    }

    /**
     * Binds the framebuffer to the frame
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

        this.texture = new FrameBufferTexture(width, height);
        GL30C.glFramebufferTexture2D(GL30C.GL_FRAMEBUFFER, GL30C.GL_COLOR_ATTACHMENT0, GL30C.GL_TEXTURE_2D,
                this.texture.textureID, 0);

        int rboID = GL30C.glGenRenderbuffers();
        GL30C.glBindRenderbuffer(GL30C.GL_RENDERBUFFER, rboID);
        GL30C.glRenderbufferStorage(GL30C.GL_RENDERBUFFER, GL30C.GL_DEPTH_COMPONENT32, width, height);
        GL30C.glFramebufferRenderbuffer(GL30C.GL_FRAMEBUFFER, GL30C.GL_DEPTH_ATTACHMENT, GL30C.GL_RENDERBUFFER, rboID);

        if (GL30C.glCheckFramebufferStatus(GL30C.GL_FRAMEBUFFER) != GL30C.GL_FRAMEBUFFER_COMPLETE) {
            assert false : "Error: Framebuffer is not complete";
        }
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, 0);
    }

    /**
     * Returns the FBO id of the framebuffer
     * @return Framebuffer FBO
     */
    public int GetFBO() {
        return fboID;
    }

    /**
     * Returns the framebuffer texture
     * @return Framebuffer texture
     */
    public int GetTextureID() {
        return texture.textureID;
    }

}
