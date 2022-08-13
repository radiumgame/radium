package Radium.Editor.EditorWindows;

import Radium.Engine.Components.Rendering.Light;
import Radium.Integration.Project.Project;
import Radium.Engine.Graphics.Lighting.LightingSettings;
import Radium.Engine.Serialization.Serializer;
import Radium.Editor.EditorGUI;
import Radium.Editor.EditorWindow;
import Radium.Engine.Graphics.Shadows.Shadows;
import imgui.ImGui;

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

    
    public void Start() {

    }

    private int tempShadowQuality = Shadows.ShadowFramebufferSize;
    public void RenderGUI() {
        Radium.Engine.Graphics.Lighting.Lighting.useBlinn = EditorGUI.Checkbox("Use Blinn Lighting", Radium.Engine.Graphics.Lighting.Lighting.useBlinn);
        Radium.Engine.Graphics.Lighting.Lighting.useGammaCorrection = EditorGUI.Checkbox("Use Gamma Correction", Radium.Engine.Graphics.Lighting.Lighting.useGammaCorrection);
        Radium.Engine.Graphics.Lighting.Lighting.HDR = EditorGUI.Checkbox("Use HDR", Radium.Engine.Graphics.Lighting.Lighting.HDR);
        Radium.Engine.Graphics.Lighting.Lighting.DefaultPBR = EditorGUI.Checkbox("Default Lighting to PBR", Radium.Engine.Graphics.Lighting.Lighting.DefaultPBR);
        Radium.Engine.Graphics.Lighting.Lighting.gamma = EditorGUI.DragFloat("Gamma", Radium.Engine.Graphics.Lighting.Lighting.gamma);
        Radium.Engine.Graphics.Lighting.Lighting.exposure = EditorGUI.DragFloat("Exposure", Radium.Engine.Graphics.Lighting.Lighting.exposure);

        tempShadowQuality = EditorGUI.SliderInt("Shadow Quality", tempShadowQuality, 256, 4096);
        ImGui.sameLine();
        if (ImGui.button("Generate Shadow FrameBuffer")) {
            Shadows.ShadowFramebufferSize = tempShadowQuality;
            Shadows.Initialize();

            for (Light light : Light.lightsInScene) {
                light.shadowCubemap.Initialize();
            }
            Light.UpdateShadows();
        }
        Radium.Engine.Graphics.Lighting.Lighting.shadowSamples = EditorGUI.SliderInt("Shadow Samples", Radium.Engine.Graphics.Lighting.Lighting.shadowSamples, 1, 100);
        Radium.Engine.Graphics.Lighting.Lighting.directionalShadowBias = EditorGUI.DragFloat("Directional Shadow Bias", Radium.Engine.Graphics.Lighting.Lighting.directionalShadowBias);
        Radium.Engine.Graphics.Lighting.Lighting.pointShadowBias = EditorGUI.DragFloat("Point Shadow Bias", Radium.Engine.Graphics.Lighting.Lighting.pointShadowBias);

        if (ImGui.button("Save Lighting Settings")) {
            SaveLightingSettings();
        }

        Radium.Engine.Graphics.Lighting.Lighting.UpdateUniforms();
    }

    public static void SaveLightingSettings() {
        LightingSettings settings = new LightingSettings();
        settings.blinnLighting = Radium.Engine.Graphics.Lighting.Lighting.useBlinn;
        settings.gammaCorrection = Radium.Engine.Graphics.Lighting.Lighting.useGammaCorrection;
        settings.HDR = Radium.Engine.Graphics.Lighting.Lighting.HDR;
        settings.gamma = Radium.Engine.Graphics.Lighting.Lighting.gamma;
        settings.exposure = Radium.Engine.Graphics.Lighting.Lighting.exposure;
        settings.shadowSamples = Radium.Engine.Graphics.Lighting.Lighting.shadowSamples;
        settings.directionalShadowBias = Radium.Engine.Graphics.Lighting.Lighting.directionalShadowBias;
        settings.pointShadowBias = Radium.Engine.Graphics.Lighting.Lighting.pointShadowBias;

        Serializer.SaveInProject(settings, Project.Current().name + ".lighting");
    }

    public static void LoadLightingSettings() {
        LightingSettings settings = (LightingSettings)Serializer.LoadFromProject(Project.Current().name + ".lighting", LightingSettings.class);
        if (settings == null) {
            return;
        }

        Radium.Engine.Graphics.Lighting.Lighting.useBlinn = settings.blinnLighting;
        Radium.Engine.Graphics.Lighting.Lighting.useGammaCorrection = settings.gammaCorrection;
        Radium.Engine.Graphics.Lighting.Lighting.HDR = settings.HDR;
        Radium.Engine.Graphics.Lighting.Lighting.gamma = settings.gamma;
        Radium.Engine.Graphics.Lighting.Lighting.exposure = settings.exposure;
        Radium.Engine.Graphics.Lighting.Lighting.shadowSamples = settings.shadowSamples;
        Radium.Engine.Graphics.Lighting.Lighting.directionalShadowBias = settings.directionalShadowBias;
        Radium.Engine.Graphics.Lighting.Lighting.pointShadowBias = settings.pointShadowBias;
    }

}
