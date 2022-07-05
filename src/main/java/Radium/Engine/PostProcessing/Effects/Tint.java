package Radium.Engine.PostProcessing.Effects;

import Radium.Engine.Color.Color;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.PostProcessing.EffectField;
import Radium.Engine.PostProcessing.PostProcessingEffect;

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
