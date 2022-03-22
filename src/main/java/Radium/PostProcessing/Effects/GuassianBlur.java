package Radium.PostProcessing.Effects;

import Radium.Graphics.Shader;
import Radium.Math.Vector.Vector2;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;
import RadiumEditor.Annotations.RangeFloat;
import RadiumEditor.Annotations.RangeInt;

public class GuassianBlur extends PostProcessingEffect {

    @EffectField
    public float intensity = 5;

    public GuassianBlur() {
        name = "guassianBlur";
    }

    @Override
    public void SetUniforms(Shader shader) {
        shader.SetUniform("blurIntensity", intensity);
    }
}
