package Radium.PostProcessing.Effects;

import Radium.Graphics.Shader;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;
import RadiumEditor.Annotations.RangeFloat;

public class Bloom extends PostProcessingEffect {

    @EffectField
    @RangeFloat()
    public float threshold = 0.7f;

    @EffectField
    @RangeFloat()
    public float intensity = 1f;

    public Bloom() {
        name = "bloom";
    }

    @Override
    public void SetUniforms(Shader shader) {
        shader.SetUniform("bloomThreshold", threshold);
        shader.SetUniform("bloomIntensity", intensity);
    }
}
