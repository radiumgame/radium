package Radium.Graphics.Shader;

import Radium.Math.Random;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Util.FileUtility;
import RadiumEditor.Console;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;

import java.io.File;
import java.nio.ByteBuffer;

public class ComputeShader {

    public String filepath;
    private String content;

    private File f;

    private int program;
    private int shader;

    private Vector2 size = new Vector2(1920, 1080);

    private int screenTex;
    private int currentBinding;

    public ComputeShader(String filepath, int resultBinding) {
        this.filepath = filepath;
        f = new File(filepath);
        content = FileUtility.ReadFile(f);

        currentBinding = resultBinding;

        CreateShader();
        CreateScreen(resultBinding);
    }

    public int Dispatch(int x, int y, int z) {
        GL43.glUseProgram(program);
        GL43.glDispatchCompute((int)size.x / x, (int)size.y / y, z);
        GL43.glMemoryBarrier(GL43.GL_ALL_BARRIER_BITS);

        return screenTex;
    }

    public void BindTexture(int unit) {
        GL45.glBindTextureUnit(unit, screenTex);
    }

    public void SetUniform(String name, int value) {
        GL45.glUniform1i(GL43.glGetUniformLocation(program, name), value);
    }

    public void SetUniform(String name, float value) {
        GL45.glUniform1f(GL43.glGetUniformLocation(program, name), value);
    }

    public void SetUniform(String name, Vector2 value) {
        GL45.glUniform2f(GL43.glGetUniformLocation(program, name), value.x, value.y);
    }

    public void SetUniform(String name, Vector3 value) {
        GL45.glUniform3f(GL43.glGetUniformLocation(program, name), value.x, value.y, value.z);
    }

    public void SetUniform(String name, Matrix4f value) {
        GL45.glUniformMatrix4fv(GL43.glGetUniformLocation(program, name), false, value.get(new float[16]));
    }

    public void SetSize(Vector2 size) {
        this.size = size;

        CreateScreen(currentBinding);
    }

    public int GetProgram() {
        return program;
    }

    public int GetTexture() {
        return screenTex;
    }

    private void CreateShader() {
        program = GL43.glCreateProgram();
        shader = GL43.glCreateShader(GL43.GL_COMPUTE_SHADER);

        GL43.glShaderSource(shader, content);
        GL43.glCompileShader(shader);

        if (GL43.glGetShaderi(shader, GL43.GL_COMPILE_STATUS) == GL43.GL_FALSE) {
            Console.Error(GL43.glGetShaderInfoLog(shader));
        }

        GL43.glAttachShader(program, shader);
        GL43.glLinkProgram(program);

        if (GL43.glGetProgrami(program, GL43.GL_LINK_STATUS) == GL43.GL_FALSE) {
            Console.Error(GL43.glGetProgramInfoLog(program));
        }
    }

    private void CreateScreen(int unit) {
        screenTex = GL45.glCreateTextures(GL45.GL_TEXTURE_2D);
        GL45.glBindTexture(GL45.GL_TEXTURE_2D, screenTex);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_MIN_FILTER, GL45.GL_NEAREST);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_MAG_FILTER, GL45.GL_NEAREST);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_WRAP_S, GL45.GL_CLAMP_TO_EDGE);
        GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_WRAP_T, GL45.GL_CLAMP_TO_EDGE);
        GL45.glTextureStorage2D(screenTex, 1, GL45.GL_RGBA32F, (int)size.x, (int)size.y);
        GL45.glBindImageTexture(unit, screenTex, 0, false, 0, GL45.GL_WRITE_ONLY, GL45.GL_RGBA32F);
    }

}
