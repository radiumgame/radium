package Radium.PostProcessing.Effects;

import Radium.Graphics.Shader.Shader;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;

public class GuassianBlur extends PostProcessingEffect {

    @EffectField
    public float intensity = 5;

    public GuassianBlur() {
        name = "guassianBlur";
    }

    
    public void SetUniforms(Shader shader) {
        shader.SetUniform("blurIntensity", intensity);
    }
}
