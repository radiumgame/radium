package Radium.PostProcessing;

import Radium.Graphics.Shader;

import java.util.UUID;

public class PostProcessingEffect {

    public String id = UUID.randomUUID().toString();
    public String name = "effect";

    private boolean enabled = true;

    public void SetUniforms(Shader shader) {}

    public void Enable(Shader shader) {
        shader.Bind();
        shader.SetUniform(name, true);
        shader.Unbind();
        enabled = true;
    }

    public void Disable(Shader shader) {
        shader.Bind();
        shader.SetUniform(name, false);
        shader.Unbind();
        enabled = false;
    }

    public String GetName() {
        return name;
    }

    public boolean Enabled() {
        return enabled;
    }

}
