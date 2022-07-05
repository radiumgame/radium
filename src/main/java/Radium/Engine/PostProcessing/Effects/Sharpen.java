package Radium.Engine.PostProcessing.Effects;

import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.PostProcessing.EffectField;
import Radium.Engine.PostProcessing.PostProcessingEffect;

public class Sharpen extends PostProcessingEffect {

    @EffectField
    public float intensity = 0.8f;

    public Sharpen() {
        name = "sharpen";
    }

    
    public void SetUniforms(Shader shader) {
        shader.SetUniform("sharpenIntensity", intensity);
    }
}
