package Radium.Engine.PostProcessing.Effects;

import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.PostProcessing.EffectField;
import Radium.Engine.PostProcessing.PostProcessingEffect;

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
