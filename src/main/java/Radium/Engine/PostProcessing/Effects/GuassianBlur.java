package Radium.Engine.PostProcessing.Effects;

import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.PostProcessing.EffectField;
import Radium.Engine.PostProcessing.PostProcessingEffect;

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
