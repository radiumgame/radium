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
        }

        if (ImGui.button("Save Lighting Settings")) {
            SaveLightingSettings();
        }
    }

    public static void SaveLightingSettings() {
        LightingSettings settings = new LightingSettings();
        settings.blinnLighting = Radium.Engine.Graphics.Lighting.Lighting.useBlinn;
        settings.gammaCorrection = Radium.Engine.Graphics.Lighting.Lighting.useGammaCorrection;
        settings.HDR = Radium.Engine.Graphics.Lighting.Lighting.HDR;
        settings.gamma = Radium.Engine.Graphics.Lighting.Lighting.gamma;
        settings.exposure = Radium.Engine.Graphics.Lighting.Lighting.exposure;

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
    }

}
