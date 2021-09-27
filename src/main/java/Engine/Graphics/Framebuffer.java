package Engine.Graphics;

import org.lwjgl.opengl.GL30C;

public class Framebuffer {

    private int fboID;
    private FrameBufferTexture texture;

    public Framebuffer(int width, int height) {
        GenerateFramebuffer(width, height);
    }

    public void Bind() {
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, fboID);
    }

    public void Unbind() {
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, 0);
    }

    public void GenerateFramebuffer(int width, int height) {
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

    public int GetFBO() {
        return fboID;
    }

    public int GetTextureID() {
        return texture.textureID;
    }

}
