package Radium.PostProcessing.Effects;

import Radium.Graphics.Shader.Shader;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;

public class Pixelize extends PostProcessingEffect {

    @EffectField
    public int pixelSize = 16;

    public Pixelize() {
        name = "pixelize";
    }

    
    public void SetUniforms(Shader shader) {
        shader.SetUniform("pixelSize", pixelSize);
    }
}
