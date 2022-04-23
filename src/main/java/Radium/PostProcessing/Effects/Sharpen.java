package Radium.PostProcessing.Effects;

import Radium.Graphics.Shader.Shader;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;

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
