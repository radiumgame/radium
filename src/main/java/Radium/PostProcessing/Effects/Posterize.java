package Radium.PostProcessing.Effects;

import Radium.Graphics.Shader;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;

public class Posterize extends PostProcessingEffect {

    @EffectField
    public int levels = 10;

    public Posterize() {
        name = "posterize";
    }

    
    public void SetUniforms(Shader shader) {
        shader.SetUniform("posterizeLevels", levels);
    }
}
