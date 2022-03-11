package Radium.PostProcessing;

import Radium.Application;
import Radium.Graphics.Framebuffer.Framebuffer;
import Radium.Graphics.Shader;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.PostProcessing.Effects.Tint;
import Radium.Time;
import Radium.Window;
import RadiumEditor.Console;
import RadiumEditor.EditorWindow;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import org.reflections.Reflections;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PostProcessing {

    private static Shader shader;
    private static Framebuffer framebuffer;

    private static List<PostProcessingEffect> effects = new ArrayList<>();

    private static Reflections reflections = new Reflections("");
    public static List<PostProcessingEffect> effectList = new ArrayList<>();

    private static int RECT;

    protected PostProcessing() {}

    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/PostProcessing/vert.glsl", "EngineAssets/Shaders/PostProcessing/frag.glsl");
        framebuffer = new Framebuffer(1920, 1080);

        Set<Class<? extends PostProcessingEffect>> effectSet = reflections.getSubTypesOf(PostProcessingEffect.class);
        for (Class<? extends PostProcessingEffect> effect : effectSet) {
            try {
                Object instance = effect.getDeclaredConstructor().newInstance();
                PostProcessingEffect newEffect = (PostProcessingEffect)instance;
                effectList.add(newEffect);
            }
            catch (Exception e) {
                Console.Error(e);
            }
        }

        GenerateRectangle();
    }

    public static void Render() {
        framebuffer.Bind();
        shader.Bind();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        shader.SetUniform("playing", Application.Playing);
        shader.SetUniform("time", Time.GetTime());

        GL30.glBindVertexArray(RECT);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL13.glActiveTexture(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Window.GetFrameBuffer().GetTextureID());

        for (PostProcessingEffect effect : effects) {
            effect.SetUniforms(shader);
        }

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        shader.Unbind();
        framebuffer.Unbind();
    }

    public static int GetTexture() {
        return framebuffer.GetTextureID();
    }

    public static void AddEffect(PostProcessingEffect effect) {
        for (PostProcessingEffect effect1 : effects) {
            if (effect1.getClass() == effect.getClass()) {
                Console.Error("Cannot add the same post processing effect twice");
                return;
            }
        }

        effects.add(effect);
        effect.Enable(shader);
    }

    public static void RemoveEffect(PostProcessingEffect effect) {
        effect.Disable(shader);
        effects.remove(effect);
    }

    public static void RemoveEffect(String id) {
        for (PostProcessingEffect effect : effects) {
            if (effect.id == id) {
                RemoveEffect(effect);
                break;
            }
        }
    }

    public static List<PostProcessingEffect> GetEffects() {
        return effects;
    }

    private static void GenerateRectangle() {
        Vector3[] vertices = {
            new Vector3(-1, -1, 0),
            new Vector3(1, -1, 0),
            new Vector3(-1, 1, 0),

            new Vector3(-1, 1, 0),
            new Vector3(1, 1, 0),
            new Vector3(1, -1, 0),
        };
        Vector2[] textureCoords = {
            new Vector2(0, 0),
            new Vector2(1, 0),
            new Vector2(0, 1),

            new Vector2(0, 1),
            new Vector2(1, 1),
            new Vector2(1, 0),
        };

        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
        float[] positionData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            positionData[i * 3] = vertices[i].x;
            positionData[i * 3 + 1] = vertices[i].y;
            positionData[i * 3 + 2] = vertices[i].z;
        }
        positionBuffer.put(positionData).flip();

        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(textureCoords.length * 2);
        float[] textureData = new float[textureCoords.length * 2];
        for (int i = 0; i < textureCoords.length; i++) {
            textureData[i * 2] = textureCoords[i].x;
            textureData[i * 2 + 1] = textureCoords[i].y;
        }
        textureBuffer.put(textureData).flip();

        RECT = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(RECT);
        StoreData(positionBuffer, 0, 3);
        StoreData(textureBuffer, 1, 2);
    }

    private static void StoreData(FloatBuffer buffer, int index, int size)
    {
        int bufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

}
