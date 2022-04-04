package RadiumEditor.EditorWindows;

import Integration.Project.Project;
import Radium.Graphics.Lighting.LightingSettings;
import Radium.Serialization.Serializer;
import RadiumEditor.Console;
import RadiumEditor.EditorGUI;
import RadiumEditor.EditorWindow;
import Radium.Graphics.Shadows.Shadows;
import imgui.ImGui;

import java.io.File;

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
        Shadows.ShadowFramebufferSize = shadowQuality;
        ImGui.sameLine();
        if (ImGui.button("Generate Shadow FrameBuffer")) {
            if (Shadows.ShadowFramebufferSize != shadowQuality) {
                Shadows.CreateFramebuffer();
            }
        }

        if (ImGui.button("Save Lighting Settings")) {
            SaveLightingSettings();
        }
    }

    public static void SaveLightingSettings() {
        LightingSettings settings = new LightingSettings();
        settings.blinnLighting = Radium.Graphics.Lighting.Lighting.useBlinn;
        settings.gammaCorrection = Radium.Graphics.Lighting.Lighting.useGammaCorrection;
        settings.HDR = Radium.Graphics.Lighting.Lighting.HDR;
        settings.gamma = Radium.Graphics.Lighting.Lighting.gamma;
        settings.exposure = Radium.Graphics.Lighting.Lighting.exposure;

        Serializer.SaveInProject(settings, Project.Current().name + ".lighting");
    }

    public static void LoadLightingSettings() {
        LightingSettings settings = (LightingSettings)Serializer.LoadFromProject(Project.Current().name + ".lighting", LightingSettings.class);
        if (settings == null) {
            return;
        }

        Radium.Graphics.Lighting.Lighting.useBlinn = settings.blinnLighting;
        Radium.Graphics.Lighting.Lighting.useGammaCorrection = settings.gammaCorrection;
        Radium.Graphics.Lighting.Lighting.HDR = settings.HDR;
        Radium.Graphics.Lighting.Lighting.gamma = settings.gamma;
        Radium.Graphics.Lighting.Lighting.exposure = settings.exposure;
    }

}
