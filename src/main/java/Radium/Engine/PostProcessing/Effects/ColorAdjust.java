package Radium.Engine.PostProcessing.Effects;

import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.PostProcessing.EffectField;
import Radium.Engine.PostProcessing.PostProcessingEffect;
import Radium.Editor.Annotations.RangeFloat;

public class ColorAdjust extends PostProcessingEffect {

    @EffectField
    @RangeFloat()
    public float contrast = 0.3f;

    public ColorAdjust() {
        name = "colorAdjust";
    }

    
    public void SetUniforms(Shader shader) {
        shader.SetUniform("colorContrast", contrast);
    }
}
