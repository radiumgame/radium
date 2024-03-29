package Radium.Engine.Graphics.Framebuffer;

import Radium.Editor.Console;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL44;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

/**
 * Texture for framebuffer to use
 */
public class FrameBufferTexture {

    /**
     * Filepath of texture
     */
    public String filepath;
    /**
     * Texture ID for renderer to use
     */
    public int textureID;
    /**
     * Name of texture
     */
    public String name;

    /**
     * Width of texture
     */
    public int width;
    /**
     * Height of texture
     */
    public int height;

    /**
     * Create framebuffer texture from path
     * @param filepath Texture to use
     */
    public FrameBufferTexture(String filepath) {
        try {
            this.filepath = filepath;
            this.name = new File(filepath).getName();

            textureID = glGenTextures();

            glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -0.4f);

            glBindTexture(GL_TEXTURE_2D, textureID);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            IntBuffer width = BufferUtils.createIntBuffer(1);
            IntBuffer height = BufferUtils.createIntBuffer(1);
            IntBuffer channels = BufferUtils.createIntBuffer(1);
            ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

            this.width = width.get(0);
            this.height = height.get(0);

            if (image != null) {
                if (channels.get(0) == 3) {
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                            0, GL_RGB, GL_UNSIGNED_BYTE, image);
                } else if (channels.get(0) == 4) {
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                            0, GL_RGBA, GL_UNSIGNED_BYTE, image);
                } else {
                    assert false : "Error: (Texture) Unknown number of channels '" + channels.get(0) + "'";
                }
            } else {
                assert false : "Error: (Texture) Could not load image '" + filepath + "'";
            }

            stbi_image_free(image);
        }
        catch (Exception e) {
            Console.Error(e);
        }
    }

    /**
     * Generate empty texture
     * @param width Width to use
     * @param height Height to use
     */
    public FrameBufferTexture(int width, int height) {
        this.filepath = "Generated";

        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    public FrameBufferTexture(int width, int height, int samples) {
        this.filepath = "Generated";

        textureID = glGenTextures();
        glBindTexture(GL44.GL_TEXTURE_2D_MULTISAMPLE, textureID);

        glTexParameteri(GL44.GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL44.GL_TEXTURE_2D_MULTISAMPLE, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        GL44.glTexImage2DMultisample(GL44.GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGB, width, height, true);
        glBindTexture(GL44.GL_TEXTURE_2D_MULTISAMPLE, 0);
    }

    public void Destroy() {
        glDeleteTextures(textureID);
    }

    /**
     * Returns the buffer of the texture
     * @return Texture buffer
     */
    public ByteBuffer GetBuffer() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            ByteBuffer image = STBImage.stbi_load(filepath, w, h, comp, 4);
            if (image == null) {
                System.err.println("Couldn't load " + filepath);
            }

            return image;
        }
        catch (Exception e) {
            Console.Error(e);
        }

        return null;
    }

}
