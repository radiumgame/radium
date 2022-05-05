// https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/nanovg/NanoVGUtils.java

package Radium.UI.NanoVG;

import Radium.Color;
import Radium.Components.UI.Image;
import Radium.Components.UI.Panel;
import Radium.Components.UI.Text;
import RadiumEditor.Console;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nuklear.Nuklear;

public class NVGUtils {

    protected NVGUtils() {}

    public static void Image(Image image) {
        NVGRenderQueue.renderQueue.add(() -> {
            NanoVG.nvgBeginPath(NVG.Instance);
            NanoVG.nvgRect(NVG.Instance, image.position.x, image.position.y, image.size.x, image.size.y);

            if (image.texture.filepath != null && !image.texture.filepath.isEmpty()) {
                NanoVG.nvgFillPaint(NVG.Instance, image.pattern);
            } else {
                NanoVG.nvgFillColor(NVG.Instance, CreateColor(image.color));
            }

            NanoVG.nvgFill(NVG.Instance);
        });
    }

    public static void Panel(Panel panel) {
        NVGRenderQueue.renderQueue.add(() -> {
            NanoVG.nvgBeginPath(NVG.Instance);
            NanoVG.nvgRect(NVG.Instance, 0, 0, 1920, 1080);
            NanoVG.nvgFillColor(NVG.Instance, CreateColor(panel.color));
            NanoVG.nvgFill(NVG.Instance);
        });
    }

    public static void Text(Text text) {
        NVGRenderQueue.renderQueue.add(() -> {
            NanoVG.nvgFontSize(NVG.Instance, text.fontSize);
            NanoVG.nvgFontFace(NVG.Instance, text.font.name());
            NanoVG.nvgFontBlur(NVG.Instance, text.fontBlur);
            NanoVG.nvgTextAlign(NVG.Instance, text.GetAlign());
            NanoVG.nvgFillColor(NVG.Instance, CreateColor(text.color));
            NanoVG.nvgText(NVG.Instance, text.Position.x, text.Position.y, text.text);
        });
    }

    public static NVGColor CreateColor(Color color) {
        NVGColor col = NVGColor.create();
        col.r(color.r);
        col.g(color.g);
        col.b(color.b);
        col.a(color.a);

        return col;
    }

}
