package Radium.PostProcessing.Effects;

import Radium.Graphics.Shader;
import Radium.PostProcessing.EffectField;
import Radium.PostProcessing.PostProcessingEffect;

public class Pixelize extends PostProcessingEffect {

    @EffectField
    public int pixelSize = 16;

    public Pixelize() {
        name = "pixelize";
    }

    @Override
    public void SetUniforms(Shader shader) {
        shader.SetUniform("pixelSize", pixelSize);
    }
}
