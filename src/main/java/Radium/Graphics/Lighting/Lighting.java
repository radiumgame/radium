package Radium.Graphics.Lighting;

import Radium.Components.Rendering.Light;
import Radium.Graphics.Renderers.Renderers;
import Radium.Graphics.Shader.Shader;

/**
 * Lighting settings
 */
public final class Lighting {

    private static Shader shader;

    /**
     * Ambient light intensity
     */
    public static float ambient = 0.3f;
    /**
     * Determines whether to use blinn lighting
     */
    public static boolean useBlinn = false;
    /**
     * Determines whether to use gamma correction
     */
    public static boolean useGammaCorrection = false;
    /**
     * Determines whether to use High Dynamic Range
     */
    public static boolean HDR = false;
    /**
     * Gamma intensity for gamma correction
     */
    public static float gamma = 2.2f;
    /**
     * Exposure for HDR
     */
    public static float exposure = 0.1f;

    protected Lighting() {}

    /**
     * Initialize the lit shader
     */
    public static void Initialize() {
        shader = Renderers.renderers.get(1).shader;
    }

    /**
     * Update the lighting uniforms
     */
    public static void UpdateUniforms() {
        shader.Bind();

        shader.SetUniform("lightCount", Light.LightIndex + 1);
        shader.SetUniform("ambient", ambient);
        shader.SetUniform("gamma", gamma);

        shader.SetUniform("useBlinn", useBlinn);
        shader.SetUniform("useGammaCorrection", useGammaCorrection);
        shader.SetUniform("HDR", HDR);
        shader.SetUniform("exposure", exposure);

        shader.Unbind();
    }

}
