package Radium.Graphics.Lighting;

import Radium.Components.Rendering.Light;
import Radium.Graphics.Renderers.Renderers;
import Radium.Graphics.Shader;

public final class Lighting {

    private static Shader shader;

    private static float ambient = 0.3f;
    private static float gamma = 2.2f;

    public static boolean useBlinn = false;
    public static boolean useGammaCorrection = false;

    protected Lighting() {}

    public static void Initialize() {
        shader = Renderers.renderers.get(1).shader;
    }

    public static void UpdateUniforms() {
        shader.Bind();

        shader.SetUniform("lightCount", Light.LightIndex + 1);
        shader.SetUniform("ambient", ambient);
        shader.SetUniform("gamma", gamma);

        shader.SetUniform("useBlinn", useBlinn);
        shader.SetUniform("useGammaCorrection", useGammaCorrection);

        shader.Unbind();
    }

}
