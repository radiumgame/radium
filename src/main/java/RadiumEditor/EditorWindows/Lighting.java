package RadiumEditor.EditorWindows;

import RadiumEditor.EditorGUI;
import RadiumEditor.EditorWindow;
import Radium.Graphics.Shadows.Shadows;

public class Lighting extends EditorWindow {

    public Lighting() {
        MenuName = "Lighting";
    }

    @Override
    public void Start() {

    }

    @Override
    public void RenderGUI() {
        StartWindow();

        Radium.Graphics.Lighting.Lighting.useBlinn = EditorGUI.Checkbox("Blinn Lighting", Radium.Graphics.Lighting.Lighting.useBlinn);
        Radium.Graphics.Lighting.Lighting.useGammaCorrection = EditorGUI.Checkbox("Gamma Correction", Radium.Graphics.Lighting.Lighting.useGammaCorrection);

        int shadowQuality = EditorGUI.SliderInt("Shadow Quality", Shadows.ShadowFramebufferSize, 256, 4096);
        if (Shadows.ShadowFramebufferSize != shadowQuality) {
            Shadows.ShadowFramebufferSize = shadowQuality;
            Shadows.CreateFramebuffer();
        }

        EndWindow();
    }

}
