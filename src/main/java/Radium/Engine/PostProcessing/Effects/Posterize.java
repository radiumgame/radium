package Radium.Engine.PostProcessing.Effects;

import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.PostProcessing.EffectField;
import Radium.Engine.PostProcessing.PostProcessingEffect;

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
