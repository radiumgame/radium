package Radium.PostProcessing.Effects;

import Radium.Graphics.Shader;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;
import RadiumEditor.Annotations.RangeFloat;

public class ColorAdjust extends PostProcessingEffect {

    @EffectField
    @RangeFloat()
    public float contrast = 0.3f;

    public ColorAdjust() {
        name = "colorAdjust";
    }

    @Override
    public void SetUniforms(Shader shader) {
        shader.SetUniform("colorContrast", contrast);
    }
}
