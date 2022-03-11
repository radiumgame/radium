package Radium.PostProcessing.Effects;

import Radium.Graphics.Shader;
import Radium.Math.Vector.Vector2;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;
import RadiumEditor.Annotations.RangeFloat;

public class GuassianBlur extends PostProcessingEffect {

    @EffectField
    @RangeFloat(max = 20)
    public float intensity = 10;

    public GuassianBlur() {
        name = "guassianBlur";
    }

    @Override
    public void SetUniforms(Shader shader) {
        shader.SetUniform("targetSize", new Vector2(1920f / intensity, 1080f / intensity));
    }
}
