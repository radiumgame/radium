package Radium.Engine.Graphics.Shader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Radium.Engine.Math.Vector.*;
import Radium.Engine.Util.FileUtility;
import Radium.Engine.Util.ShaderUtility;
import Radium.Editor.Console;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;
import org.python.core.util.FileUtil;

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
	public transient String geometryFile;
	public transient File vertex, fragment, geometry;
	private transient int vertexID, fragmentID, geometryID, programID;

	private transient boolean hasValidated;

	private final HashMap<String, Integer> locations = new HashMap<>();
	private final HashMap<String, Object> values = new HashMap<>();

	public List<ShaderUniform> uniforms = new ArrayList<>();
	private final List<ShaderLibrary> libraries = new ArrayList<>();

	public static int CurrentProgram = -1;

	public Shader() {

	}

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

	public Shader(String vertexPath, String fragmentPath, String geometryPath) {
		vertex = new File(vertexPath);
		fragment = new File(fragmentPath);
		geometry = new File(geometryPath);
		vertexFile = FileUtility.LoadAsString(vertexPath);
		fragmentFile = FileUtility.LoadAsString(fragmentPath);
		geometryFile = FileUtility.LoadAsString(geometryPath);

		CreateShaderWithGeometry();
	}

	public Shader(String vertexPath, String fragmentPath, boolean compile) {
		vertex = new File(vertexPath);
		fragment = new File(fragmentPath);
		vertexFile = FileUtility.LoadAsString(vertexPath);
		fragmentFile = FileUtility.LoadAsString(fragmentPath);

		if (compile) CreateShader();
	}

	public Shader(String vertexPath, String fragmentPath, String geometryPath, boolean compile) {
		vertex = new File(vertexPath);
		fragment = new File(fragmentPath);
		geometry = new File(geometryPath);
		vertexFile = FileUtility.LoadAsString(vertexPath);
		fragmentFile = FileUtility.LoadAsString(fragmentPath);
		geometryFile = FileUtility.LoadAsString(geometryPath);

		if (compile) CreateShaderWithGeometry();
	}

	public void Compile() {
		if (geometry != null) {
			vertexFile = FileUtility.LoadAsString(vertex.getAbsolutePath());
			fragmentFile = FileUtility.LoadAsString(fragment.getAbsolutePath());
			geometryFile = FileUtility.LoadAsString(geometry.getAbsolutePath());
			CreateShaderWithGeometry();
		} else {
			vertexFile = FileUtility.LoadAsString(vertex.getAbsolutePath());
			fragmentFile = FileUtility.LoadAsString(fragment.getAbsolutePath());
			CreateShader();
		}
	}

	public void Compile(CompileMode compileMode) {
		if (compileMode == CompileMode.File) {
			Compile();
			return;
		}

		if (geometry != null) {
			CreateShaderWithGeometry();
		} else {
			CreateShader();
		}
	}

	public void CompileWithGeometry() {
		CreateShaderWithGeometry();
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

		String[] split = fragmentFile.split("out vec4");
		StringBuilder fragmentSource = new StringBuilder(split[0] + "\n");
		for (ShaderLibrary library : libraries) {
			fragmentSource.append(library.content);
		}
		fragmentSource.append("out vec4").append(split[1]);
		GL20.glShaderSource(fragmentID, fragmentSource.toString());
		GL20.glCompileShader(fragmentID);
		if (GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			String error = GL20.glGetShaderInfoLog(fragmentID);

			try {
				int line = Integer.parseInt(error.split(":")[2]);

				int totalLineCount = 1;
				for (ShaderLibrary library : libraries) {
					int lineCount = library.content.split("\n").length;
					totalLineCount += lineCount;
				}
				totalLineCount += fragmentFile.split("\n").length;
				int start = totalLineCount - line;
				int lineError = fragmentFile.split("\n").length - start;

				int length = error.split(":").length;
				String errorMessage = "";
				for (int i = 3; i < length; i++) {
					errorMessage += error.split(":")[i] + ": ";
					if (errorMessage.contains("\n")) {
						break;
					}
				}
				errorMessage = errorMessage.replace("\nERROR:", "");
				Console.Error("Fragment Shader: " + errorMessage + "(Line " + lineError + ")");
				Console.Error(fragmentSource.toString().split("\n")[line - 1]);
			} catch (Exception e) {
				System.err.println(error);
			}

			return;
		}

		GL20.glAttachShader(programID, vertexID);
		GL20.glAttachShader(programID, fragmentID);

		GL20.glLinkProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			Console.Error("Program Linking: " + GL20.glGetProgramInfoLog(programID));
			return;
		}

		GL20.glDeleteShader(vertexID);
		GL20.glDeleteShader(fragmentID);

		uniforms = ShaderUtility.GetFragmentUniforms(this, new String[] {});
		hasValidated = false;
	}

	private void CreateShaderWithGeometry() {
		programID = GL20.glCreateProgram();
		vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

		GL20.glShaderSource(vertexID, vertexFile);
		GL20.glCompileShader(vertexID);

		if (GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Console.Error("Vertex Shader: " + GL20.glGetShaderInfoLog(vertexID));
			return;
		}

		geometryID = GL20.glCreateShader(GL32.GL_GEOMETRY_SHADER);
		GL20.glShaderSource(geometryID, geometryFile);
		GL20.glCompileShader(geometryID);
		if (GL20.glGetShaderi(geometryID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Console.Error("Geometry Shader: " + GL20.glGetShaderInfoLog(geometryID));
			return;
		}

		fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

		String[] split = fragmentFile.split("out vec4");
		StringBuilder fragmentSource = new StringBuilder(split[0] + "\n");
		for (ShaderLibrary library : libraries) {
			fragmentSource.append(library.content);
		}
		fragmentSource.append("out vec4").append(split[1]);
		GL20.glShaderSource(fragmentID, fragmentSource.toString());
		GL20.glCompileShader(fragmentID);
		if (GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			String error = GL20.glGetShaderInfoLog(fragmentID);

			try {
				int line = Integer.parseInt(error.split(":")[2]);

				int totalLineCount = 1;
				for (ShaderLibrary library : libraries) {
					int lineCount = library.content.split("\n").length;
					totalLineCount += lineCount;
				}
				totalLineCount += fragmentFile.split("\n").length;
				int start = totalLineCount - line;
				int lineError = fragmentFile.split("\n").length - start;

				int length = error.split(":").length;
				String errorMessage = "";
				for (int i = 3; i < length; i++) {
					errorMessage += error.split(":")[i] + ": ";
					if (errorMessage.contains("\n")) {
						break;
					}
				}
				errorMessage = errorMessage.replace("\nERROR:", "");
				Console.Error("Fragment Shader: " + errorMessage + "(Line " + lineError + ")");
				Console.Error(fragmentSource.toString().split("\n")[line - 1]);
			} catch (Exception e) {
				Console.Error(error);
			}

			return;
		}

		GL20.glAttachShader(programID, vertexID);
		GL20.glAttachShader(programID, fragmentID);
		GL20.glAttachShader(programID, geometryID);

		GL20.glLinkProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			Console.Error("Program Linking: " + GL20.glGetProgramInfoLog(programID));
			return;
		}

		GL20.glDeleteShader(vertexID);
		GL20.glDeleteShader(fragmentID);
		GL20.glDeleteShader(geometryID);

		uniforms = ShaderUtility.GetFragmentUniforms(this, new String[] {});
		hasValidated = false;
	}

	public void Validate() {
		if (hasValidated) return;

		GL20.glValidateProgram(programID);
		if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
			Console.Error("Program Validation: " + GL20.glGetProgramInfoLog(programID));
		}

		hasValidated = true;
	}

	/**
	 * Returns uniform location of a uniform by name
	 * @param name Name of uniform
	 * @return Uniform location
	 */
	public int GetUniformLocation(String name) {
		if (locations.containsKey(name)) {
			return locations.get(name);
		}

		int location = GL20.glGetUniformLocation(programID, name);
		locations.put(name, location);
		return location;
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, float value) {
		Object val = values.get(name);
		if (val == null) {
			values.put(name, value);
		} else {
			if (val.equals(value)) return;
		}

		GL20.glUniform1f(GetUniformLocation(name), value);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, int value) {
		Object val = values.get(name);
		if (val == null) {
			values.put(name, value);
		} else {
			if (val.equals(value)) return;
		}

		GL20.glUniform1i(GetUniformLocation(name), value);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, boolean value) {
		Object val = values.get(name);
		if (val == null) {
			values.put(name, value);
		} else {
			if (val.equals(value)) return;
		}

		GL20.glUniform1i(GetUniformLocation(name), value ? 1 : 0);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, Vector2 value) {
		Object val = values.get(name);
		if (val == null) {
			values.put(name, value);
		} else {
			if (val.equals(value)) return;
		}

		GL20.glUniform2f(GetUniformLocation(name), value.x, value.y);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, Vector3 value) {
		Object val = values.get(name);
		if (val == null) {
			values.put(name, value);
		} else {
			if (val.equals(value)) return;
		}

		GL20.glUniform3f(GetUniformLocation(name), value.x, value.y, value.z);
	}

	public void SetUniform(String name, float x, float y, float z, float w) {
		GL20.glUniform4f(GetUniformLocation(name), x, y, z, w);
	}

	/**
	 * Sets the uniforms value by name
	 * @param name Name of uniform
	 * @param value Value to set
	 */
	public void SetUniform(String name, Matrix4f value) {
		if (value == null) return;

		Object val = values.get(name);
		if (val == null) {
			values.put(name, value);
		} else {
			if (val.equals(value)) return;
		}

		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			GL20.glUniformMatrix4fv(GetUniformLocation(name), false, fb);
		}
	}

	public void AddUniform(String name) {
		locations.put(name, GetUniformLocation(name));
	}

	/**
	 * Binds the shader
	 */
	public void Bind() {
		if (CurrentProgram == programID) return;
		CurrentProgram = programID;

		GL20.glUseProgram(programID);
	}

	/**
	 * Unbinds the shader
	 */
	public void Unbind() {
		CurrentProgram = -1;
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

	public static Shader Load(String v, String f) {
		Shader shader = new Shader();
		shader.vertex = null;
		shader.fragment = null;
		shader.vertexFile = v;
		shader.fragmentFile = f;
		shader.Compile(CompileMode.NoFile);

		return shader;
	}

	private enum CompileMode {

		NoFile,
		File

	}

}