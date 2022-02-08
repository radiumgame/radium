package RadiumEditor.EditorWindows;

import RadiumEditor.EditorGUI;
import RadiumEditor.EditorWindow;
import Radium.Graphics.Shadows.Shadows;

/**
 * Lighting settings
 */
public class Lighting extends EditorWindow {

    /**
     * Creates empty instance
     */
    public Lighting() {
        MenuName = "Lighting";
    }

    @Override
    public void Start() {

    }

    @Override
    public void RenderGUI() {
        Radium.Graphics.Lighting.Lighting.useBlinn = EditorGUI.Checkbox("Use Blinn Lighting", Radium.Graphics.Lighting.Lighting.useBlinn);
        Radium.Graphics.Lighting.Lighting.useGammaCorrection = EditorGUI.Checkbox("Use Gamma Correction", Radium.Graphics.Lighting.Lighting.useGammaCorrection);
        Radium.Graphics.Lighting.Lighting.HDR = EditorGUI.Checkbox("Use HDR", Radium.Graphics.Lighting.Lighting.HDR);
        Radium.Graphics.Lighting.Lighting.gamma = EditorGUI.DragFloat("Gamma", Radium.Graphics.Lighting.Lighting.gamma);
        Radium.Graphics.Lighting.Lighting.exposure = EditorGUI.DragFloat("Exposure", Radium.Graphics.Lighting.Lighting.exposure);

        int shadowQuality = EditorGUI.SliderInt("Shadow Quality", Shadows.ShadowFramebufferSize, 256, 4096);
        if (Shadows.ShadowFramebufferSize != shadowQuality) {
            Shadows.ShadowFramebufferSize = shadowQuality;
            Shadows.CreateFramebuffer();
        }
    }

}
