package Radium.PostProcessing.Effects;

import Radium.Color.Color;
import Radium.Graphics.Shader.Shader;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;

public class Tint extends PostProcessingEffect {

    @EffectField
    public Color color = new Color(1f, 1f, 1f, 1f);

    public Tint() {
        name = "tint";
    }

    
    public void SetUniforms(Shader shader) {
        shader.SetUniform("tintColor", color.ToVector3());
    }
}
