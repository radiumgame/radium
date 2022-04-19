package Radium.PostProcessing.Effects;

import Radium.Color;
import Radium.Graphics.Shader;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;
import RadiumEditor.Annotations.RangeFloat;

public class Vignette extends PostProcessingEffect {

    @EffectField
    public Color color = new Color(0, 0, 0);

    @EffectField
    @RangeFloat
    public float innerRadius = 0.4f;

    @EffectField
    @RangeFloat
    public float outerRadius = 0.65f;

    @EffectField
    @RangeFloat()
    public float intensity = 0.6f;

    public Vignette() {
        name = "vignette";
    }

    
    public void SetUniforms(Shader shader) {
        shader.SetUniform("vignetteColor", color.ToVector3());
        shader.SetUniform("innerVignetteRadius", innerRadius);
        shader.SetUniform("outerVignetteRadius", outerRadius);
        shader.SetUniform("vignetteIntensity", intensity);
    }
}
