package Editor.EditorWindows;

import Editor.EditorGUI;
import Editor.EditorWindow;
import Engine.Graphics.Shadows.Shadows;

import java.awt.*;

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

        Engine.Graphics.Lighting.Lighting.useBlinn = EditorGUI.Checkbox("Blinn Lighting", Engine.Graphics.Lighting.Lighting.useBlinn);
        Engine.Graphics.Lighting.Lighting.useGammaCorrection = EditorGUI.Checkbox("Gamma Correction", Engine.Graphics.Lighting.Lighting.useGammaCorrection);

        int shadowQuality = EditorGUI.SliderInt("Shadow Quality", Shadows.ShadowFramebufferSize, 256, 4096);
        if (Shadows.ShadowFramebufferSize != shadowQuality) {
            Shadows.ShadowFramebufferSize = shadowQuality;
            Shadows.CreateFramebuffer();
        }

        EndWindow();
    }

}
