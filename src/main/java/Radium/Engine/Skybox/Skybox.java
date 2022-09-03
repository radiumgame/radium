package Radium.Engine.Skybox;

import Radium.Engine.Application;
import Radium.Engine.Graphics.*;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Math.Matrix4;
import Radium.Engine.Variables;
import Radium.Engine.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import java.io.File;

/**
 * An illusion of a sky
 */
public class Skybox {

    public static SkyboxType type = SkyboxType.Cubemap;

    protected Skybox() {}

    public static void Initialize() {
        CubemapSkybox.Initialize();
    }

    public static void Render() {
        switch (type) {
            case Cubemap -> {
                CubemapSkybox.Render();
            }
        }
    }

    public static int GetTexture() {
        switch (type) {
            case Cubemap -> {
                return CubemapSkybox.GetTexture();
            }
        }

        return 0;
    }


}
