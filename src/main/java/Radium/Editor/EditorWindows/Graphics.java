package Radium.Editor.EditorWindows;

import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
import Radium.Editor.EditorWindow;
import Radium.Engine.Components.Rendering.Light;
import Radium.Engine.Graphics.Shadows.Shadows;
import Radium.Engine.Util.FileUtility;
import Radium.Engine.Window;
import Radium.Integration.Project.Project;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import imgui.ImGui;

public class Graphics extends EditorWindow {

    private ShadowQuality quality;

    private static GraphicsData data;

    public Graphics() {
        MenuName = "Graphics";
    }

    @Override
    public void Start() {
        GetQuality();
    }

    @Override
    public void RenderGUI() {
        ShadowQuality shadowQuality = (ShadowQuality) EditorGUI.EnumSelect("Shadow Quality", quality.ordinal(), ShadowQuality.class);
        if (shadowQuality != quality) {
            quality = shadowQuality;
            SetQuality();
        }

        AntiAliasing antiAliasing = (AntiAliasing) EditorGUI.EnumSelect("Anti Aliasing", data.antiAliasing.ordinal(), AntiAliasing.class);
        if (antiAliasing != data.antiAliasing) {
            data.antiAliasing = antiAliasing;
            Window.SetSamples(GetSamples());
        }

        if (ImGui.button("Save")) {
            Save();
        }
        ImGui.sameLine();
    }

    private void SetQuality() {
        int shadowQuality = 1024;
        if (quality == ShadowQuality.Low) {
            shadowQuality = 512;
        } else if (quality == ShadowQuality.Medium) {
        } else if (quality == ShadowQuality.High) {
            shadowQuality = 2048;
        }

        Lighting.tempShadowQuality = shadowQuality;
        Shadows.ShadowFramebufferSize = shadowQuality;
        Shadows.Initialize();

        for (Light light : Light.lightsInScene) {
            light.shadowCubemap.Initialize();
        }
        Light.UpdateShadows();
    }

    private void GetQuality() {
        float quality = Shadows.ShadowFramebufferSize;
        if (quality <= 512) {
            this.quality = ShadowQuality.Low;
        } else if (quality <= 1024) {
            this.quality = ShadowQuality.Medium;
        } else {
            this.quality = ShadowQuality.High;
        }
    }

    public static int GetSamples() {
        int samples = 1;
        if (data.antiAliasing == AntiAliasing.None) {
        } else if (data.antiAliasing == AntiAliasing.Low) {
            samples = 2;
        } else if (data.antiAliasing == AntiAliasing.Medium) {
            samples = 4;
        } else if (data.antiAliasing == AntiAliasing.High) {
            samples = 8;
        }

        return samples;
    }

    private void Save() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
        try {
            mapper.writeValue(new java.io.File(Project.Current().root + "/" + Project.Current().name + ".graphics"), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Load() {
        java.io.File file = new java.io.File(Project.Current().root + "/" + Project.Current().name + ".graphics");
        if (!file.exists()) {
            data = new GraphicsData();
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            data = mapper.readValue(FileUtility.ReadFile(file), GraphicsData.class);
        } catch (Exception e) {
            Console.Error(e);
            data = new GraphicsData();
        }
    }

    public static enum ShadowQuality {

        Low,
        Medium,
        High,

    }

    public static enum AntiAliasing {

        None,
        Low,
        Medium,
        High,

    }

    public static class GraphicsData {

        public AntiAliasing antiAliasing = AntiAliasing.Low;

    }

}
