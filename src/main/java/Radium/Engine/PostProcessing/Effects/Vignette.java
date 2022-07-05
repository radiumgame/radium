package Radium.Engine.PostProcessing.Effects;

import Radium.Engine.Color.Color;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.PostProcessing.EffectField;
import Radium.Engine.PostProcessing.PostProcessingEffect;
import Radium.Editor.Annotations.RangeFloat;

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
