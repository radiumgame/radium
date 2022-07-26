package Radium.Engine.Graphics.Shadows;

import Radium.Editor.Console;
import org.lwjgl.opengl.*;

public class ShadowCubemap {

    private int fbo;
    private int cubemap;

    public ShadowCubemap() {
        Initialize();
    }

    public void Initialize() {
        fbo = GL30.glGenFramebuffers();

        cubemap = GL11.glGenTextures();
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubemap);
        for (int i = 0; i < 6; i++) {
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_DEPTH_COMPONENT, Shadows.ShadowFramebufferSize, Shadows.ShadowFramebufferSize, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (java.nio.ByteBuffer) null);
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL13.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, cubemap, 0);
        GL11.glDrawBuffer(GL30.GL_NONE);
        GL11.glReadBuffer(GL30.GL_NONE);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void Destroy() {
        GL30.glDeleteFramebuffers(fbo);
        GL11.glDeleteTextures(cubemap);
    }

    public void Bind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
    }

    public void Unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public int GetCubeMap() {
        return cubemap;
    }

}
