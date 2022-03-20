package Radium.PostProcessing;

import Radium.Graphics.Shader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomPostProcessingEffect {

    public transient Shader shader;
    public String shaderPath;

    public List<EffectUniform> uniforms = new ArrayList<>();

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
