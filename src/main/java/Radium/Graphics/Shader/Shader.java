package Radium.Graphics.Shader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import Radium.Math.Vector.*;
import Radium.Util.FileUtility;
import Radium.Util.ShaderUtility;
import RadiumEditor.Console;
import org.joml.Matrix4f;
import org.lwjgl.opengl.ARBShadingLanguageInclude;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryStack;
import java.io.File;

/**
 * Load shaders from external files
 */
public class Shader {

	/**
	 * Vertex shader file path
	 */
	public transient String vertexFile;
	/**
	 * Fragment shader file path
	 */
	public transient String fragmentFile;
	public transient File vertex, fragment;
	private transient int vertexID, fragmentID, programID;

	private List<ShaderUniform> uniforms = new ArrayList<>();
	private List<ShaderLibrary> libraries = new ArrayList<>();

	/**
	 * Create shader from vertex and fragment shader file paths
	 * @param vertexPath Vertex shader file path
	 * @param fragmentPath Fragment shader file path
	 */
	public Shader(String vertexPath, String fragmentPath) {
		vertex = new File(vertexPath);
		fragment = new File(fragmentPath);
		vertexFile = FileUtility.LoadAsString(vertexPath);
		fragmentFile = FileUtility.LoadAsString(fragmentPath);

		CreateShader();
	}

	public Shader(String vertexPath, String fragmentPath, boolean compile) {
		vertex = new File(vertexPath);
		fragment = new File(fragmentPath);
		vertexFile = FileUtility.LoadAsString(vertexPath);
		fragmentFile = FileUtility.LoadAsString(fragmentPath);

		if (compile) CreateShader();
	}

	public void Compile() {
		CreateShader();
	}
	
	private void CreateShader() {
		programID = GL20.glCreateProgram();
		vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

		GL20.glShaderSource(vertexID, vertexFile);
		GL20.glCompileShader(vertexID);
		
		if (GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Console.Error("Vertex Shader: " + GL20.glGetShaderInfoLog(vertexID));
			return;
		}
		
		fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

		String[] split = fragmentFile.split("void main");
		String fragmentSource = split[0] + "\n";
		for (ShaderLibrary library : libraries) {
			fragmentSource += library.content;
		}
		fragmentSource += "void main" + split[1];
		GL20.glShaderSource(fragmentID, fragmentSource);
		GL20.glCompileShader(fragmentID);
		
		if (GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Console.Error("Fragment Shader: " + GL20.glGetShaderInfoLog(fragmentID));
			return;
		}
		
		GL20.glAttachShader(programID, vertexID);
		GL20.glAttachShader(programID, fragmentID);
		
		GL20.glLinkProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			Console.Error("Program Linking: " + GL20.glGetProgramInfoLog(programID));
			return;
		}
		
		GL20.glValidateProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
			Console.Error("Program Validation: " + GL20.glGetProgramInfoLog(programID));
			return;
		}
		
		GL20.glDeleteShader(vertexID);
		GL20.glDeleteShader(fragmentID);

		uniforms = ShaderUtility.GetFragmentUniforms(this, new String[] {
				"color",
				"tex",
				"time",
				"deltaTime",
		});
	}

	/**
	 * Returns uniform location of a uniform by name
	 * @param name Name of uniform
	 * @return Uniform location
	 */
	public int GetUniformLocation(String name) {
		return GL20.glGetUniformLocation(programID, name);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, float value) {
		GL20.glUniform1f(GetUniformLocation(name), value);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, int value) {
		GL20.glUniform1i(GetUniformLocation(name), value);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, boolean value) {
		GL20.glUniform1i(GetUniformLocation(name), value ? 1 : 0);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, Vector2 value) {
		GL20.glUniform2f(GetUniformLocation(name), value.x, value.y);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, Vector3 value) {
		GL20.glUniform3f(GetUniformLocation(name), value.x, value.y, value.z);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, Matrix4f value) {
		if (value == null) return;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			GL20.glUniformMatrix4fv(GetUniformLocation(name), false, fb);
		}
	}

	/**
	 * Binds the shader
	 */
	public void Bind() {
		GL20.glUseProgram(programID);
	}

	/**
	 * Unbinds the shader
	 */
	public void Unbind() {
		GL20.glUseProgram(0);
	}

	/**
	 * Destroy the shaders buffers
	 */
	public void Destroy() {
		GL20.glDetachShader(programID, vertexID);
		GL20.glDetachShader(programID, fragmentID);
		GL20.glDeleteShader(vertexID);
		GL20.glDeleteShader(fragmentID);
		GL20.glDeleteProgram(programID);
	}

	/**
	 * Returns the shader program ID
	 * @return Shader program ID
	 */
	public int GetProgram() {
		return programID;
	}

	public List<ShaderUniform> GetUniforms() {
		return uniforms;
	}

	public void AddLibrary(ShaderLibrary library) {
		AddLibrary(library, true);
	}

	public void AddLibrary(ShaderLibrary library, boolean compile) {
		libraries.add(library);
		if (compile) Compile();
	}

}