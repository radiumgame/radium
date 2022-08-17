package Radium.Engine.Graphics;

import Radium.Editor.Console;
import Radium.Engine.Graphics.Framebuffer.FrameBufferTexture;
import Radium.Engine.OGLCommands;
import Radium.Engine.Util.ThreadUtility;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

/**
 * A class that loads and stores textures
 */
public class Texture {

    /**
     * Texture filepath
     */
    public String filepath;
    /**
     * The textures ID
     */
    private transient int textureID;

    public transient BufferedImage image;

    /**
     * The textures width
     */
    public transient int width;
    /**
     * The textures height
     */
    public transient int height;

    public transient File file;

    public static transient final HashMap<String, Texture> loadedTextures = new HashMap<>();

    private static float MAX_ANISOTROPY = 0;
    public static void Initialize() {
        MAX_ANISOTROPY = GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
    }

    /**
     * Create an empty texture
     */
    public Texture() {
        width = 0;
        height = 0;

        textureID = GL11.glGenTextures();
        filepath = "";
    }

    /**
     * Create a texture loaded from a filepath
     * @param filepath Texture file path
     */
    public Texture(String filepath) {
        this.filepath = filepath;
        if (loadedTextures.containsKey(filepath)) {
            Texture loaded = loadedTextures.get(filepath);
            this.textureID = loaded.GetTextureID();
            this.width = loaded.width;
            this.height = loaded.height;
            this.file = loaded.file;

            return;
        }

        CreateTextureMultithread();
    }

    private void CreateTexture(){
        file = new File(filepath);
        try {
            ByteBuffer image;
            int width, height;

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);

                image = STBImage.stbi_load(file.getPath(), w, h, comp, 4);
                if (image == null) {
                    Console.Error("Failed to load image " + file.getPath());
                }

                width = w.get();
                height = h.get();
            }

            this.width = width;
            this.height = height;

            textureID = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
            }

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
            STBImage.stbi_image_free(image);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    private void CreateTextureMultithread() {
        file = new File(filepath);

        ThreadUtility.Run(() -> {
            try {
                ByteBuffer image;
                int width, height;

                try (MemoryStack stack = MemoryStack.stackPush()) {
                    IntBuffer w = stack.mallocInt(1);
                    IntBuffer h = stack.mallocInt(1);
                    IntBuffer comp = stack.mallocInt(1);

                    image = STBImage.stbi_load(file.getPath(), w, h, comp, 4);
                    if (image == null) {
                        Console.Error("Failed to load image " + file.getPath());
                    }

                    width = w.get();
                    height = h.get();
                }

                this.width = width;
                this.height = height;

                OGLCommands.commands.add(() -> {
                    textureID = GL11.glGenTextures();
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_REPEAT);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_REPEAT);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

                    if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                        float amount = Math.min(4f, MAX_ANISOTROPY);
                        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
                    }

                    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
                    STBImage.stbi_image_free(image);
                    loadedTextures.put(filepath, this);
                });
            } catch (Exception e) {
                Console.Error(e);
            }
        }, "IMAGE_LOADER_" + hashCode());
    }

    public int GetTextureID() {
        if (textureID == 0) {
            CreateTexture();
        }

        return textureID;
    }


    /**
     * Returns the textures buffer
     * @return Textures buffer
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

    /**
     * Loads a cubemap from 6 textures
     * @param textures Textures to load from
     * @return A cube map ID
     */
    public static int LoadCubeMap(String[] textures) {
        int id = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);

        for (int i = 0; i < textures.length; i++) {
            FrameBufferTexture texture = new FrameBufferTexture(textures[i]);
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, texture.width, texture.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, texture.GetBuffer());
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        return id;
    }

    /**
     * Returns an empty cube map
     * @param size Resolution of cube map
     * @return Empty cube map ID
     */
    public static int CreateEmptyCubeMap(int size) {
        int id = GL11.glGenTextures();
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);

        for (int i = 0; i < 6; i++) {
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, size, size, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);

        return id;
    }

    /**
     * Loads texture from buffered image
     * @param image Buffered image to load from
     * @return A new texture
     */
    public static Texture LoadTexture(BufferedImage image) {
        Texture empty = new Texture();
        empty.width = image.getWidth();
        empty.height = image.getHeight();

        int[] pixels = new int[image.getHeight() * image.getWidth()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y=0; y < image.getHeight(); y++) {
            for (int x=0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                byte alphaComponent = (byte)((pixel >> 24) & 0xFF);

                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
            }
        }
        buffer.flip();

        empty.textureID = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, empty.GetTextureID());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(),
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        buffer.clear();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        return empty;
    }

}