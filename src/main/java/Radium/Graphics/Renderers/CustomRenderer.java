package Radium.Graphics.Renderers;

import Radium.Graphics.Shader.ShaderUniform;
import Radium.Objects.GameObject;
import Radium.Time;
import RadiumEditor.Console;

public class CustomRenderer extends Renderer {

    public void Initialize() {

    }

    public void SetUniforms(GameObject gameObject) {
        if (shader == null) return;

        shader.SetUniform("time", Time.GetTime());
        shader.SetUniform("deltaTime", Time.deltaTime);
        for (ShaderUniform uniform : shader.GetUniforms()) {
            uniform.Set();
        }
    }
}
