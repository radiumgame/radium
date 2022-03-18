package Radium.PostProcessing;

import Radium.Graphics.Shader;
import java.io.File;

public class CustomPostProcessingEffect {

    public Shader shader;
    public String shaderPath;

    public String name;

    public CustomPostProcessingEffect(String shaderPath) {
        this.shaderPath = shaderPath;
        shader = new Shader("EngineAssets/Shaders/PostProcessing/vert.glsl", shaderPath);

        name = new File(shaderPath).getName();
    }

    public CustomPostProcessingEffect(String shaderPath, Shader shader) {
        this.shaderPath = shaderPath;
        this.shader = shader;

        name = new File(shaderPath).getName();
    }

}
