package Radium.Engine.UI.NanoVG;

import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;

public class NVG {

    public static long Instance;

    protected NVG() {}

    public static void Initialize() {
        Instance = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);

        NanoVG.nvgCreateFont(Instance, "Arial", "EngineAssets/Fonts/arial.ttf");
        NanoVG.nvgCreateFont(Instance, "PTSans", "EngineAssets/Fonts/PTSans/PTSans-Regular.ttf");
        NanoVG.nvgCreateFont(Instance, "PTSansBold", "EngineAssets/Fonts/PTSans/PTSans-Bold.ttf");
        NanoVG.nvgCreateFont(Instance, "PTSansItalic", "EngineAssets/Fonts/PTSans/PTSans-Italic.ttf");
        NanoVG.nvgCreateFont(Instance, "PTSansBoldItalic", "EngineAssets/Fonts/PTSans/PTSans-BoldItalic.ttf");
    }

    public static void Destroy() {
        NanoVGGL3.nvgDelete(Instance);
    }

    public static void Render() {
        NVGRenderQueue.Render();
    }

}
