package Radium.PostProcessing.Effects;

import Radium.Graphics.Shader.Shader;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;
import RadiumEditor.Annotations.RangeFloat;
import RadiumEditor.Annotations.RangeInt;

public class Bloom extends PostProcessingEffect {

    @EffectField
    @RangeFloat()
    public float threshold = 0.7f;

    @EffectField
    @RangeFloat()
    public float intensity = 1f;

    @EffectField
    @RangeInt(min = 1, max = 6)
    public int size = 3;

    @EffectField
    @RangeFloat(max = 10)
    public float separation = 5;

    public Bloom() {
        name = "bloom";
    }

    
    public void SetUniforms(Shader shader) {
        shader.SetUniform("bloomThreshold", threshold);
        shader.SetUniform("bloomIntensity", intensity);
        shader.SetUniform("bloomSize", size);
        shader.SetUniform("bloomSeparation", separation);
    }
}
