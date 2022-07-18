package Radium.Engine.Graphics.Shadows;

import Radium.Editor.Console;
import Radium.Engine.Components.Rendering.Light;
import Radium.Engine.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Engine.Graphics.Lighting.LightType;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Graphics.Shader.ShaderLibrary;
import Radium.Engine.Util.FileUtility;
import Radium.Engine.Util.ShaderUtility;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.io.File;

/**
 * Shadow settings
 */
public class Shadows {

    /**
     * Shadow framebuffer
     */
    public static DepthFramebuffer framebuffer;
    /**
     * Shadow quality
     */
    public static int ShadowFramebufferSize = 1024;

    private static final Shader performance = new Shader("EngineAssets/Shaders/Shadows/Directional/vert.glsl", "EngineAssets/Shaders/Shadows/Directional/frag.glsl");
    private static int performanceWGeom;

    protected Shadows() {}

    /**
     * Creates shadows framebuffer
     */
    public static void Initialize() {
        framebuffer = new DepthFramebuffer(ShadowFramebufferSize, ShadowFramebufferSize);
        CreateShader();
    }

    private static void CreateShader() {
        String vert = FileUtility.ReadFile(new File("EngineAssets/Shaders/Shadows/Point/vert.glsl"));
        String frag = FileUtility.ReadFile(new File("EngineAssets/Shaders/Shadows/Point/frag.glsl"));
        String geom = FileUtility.ReadFile(new File("EngineAssets/Shaders/Shadows/Point/geom.glsl"));

        performanceWGeom = GL20.glCreateProgram();
        int vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        GL20.glShaderSource(vertexID, vert);
        GL20.glCompileShader(vertexID);

        if (GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Console.Error("Vertex Shader: " + GL20.glGetShaderInfoLog(vertexID));
            return;
        }

        int geomID = GL20.glCreateShader(GL32.GL_GEOMETRY_SHADER);

        GL20.glShaderSource(geomID, geom);
        GL20.glCompileShader(geomID);

        if (GL20.glGetShaderi(geomID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Console.Error("Geometry Shader: " + GL20.glGetShaderInfoLog(geomID));
            return;
        }

        int fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        GL20.glShaderSource(fragmentID, frag);
        GL20.glCompileShader(fragmentID);

        if (GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Console.Error("Fragment Shader: " + GL20.glGetShaderInfoLog(fragmentID));
            return;
        }

        GL20.glAttachShader(performanceWGeom, vertexID);
        GL20.glAttachShader(performanceWGeom, fragmentID);
        GL20.glAttachShader(performanceWGeom, geomID);

        GL20.glLinkProgram(performanceWGeom);
        if (GL20.glGetProgrami(performanceWGeom, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            Console.Error("Program Linking: " + GL20.glGetProgramInfoLog(performanceWGeom));
            return;
        }

        GL20.glValidateProgram(performanceWGeom);
        if (GL20.glGetProgrami(performanceWGeom, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            Console.Error("Program Validation: " + GL20.glGetProgramInfoLog(performanceWGeom));
            return;
        }

        GL20.glDeleteShader(vertexID);
        GL20.glDeleteShader(geomID);
        GL20.glDeleteShader(fragmentID);
    }

    public static int GetShader(Light light) {
        if (light.lightType == LightType.Directional) return performance.GetProgram();
        else if (light.lightType == LightType.Point) return performanceWGeom;
        else return 0;
    }

}
