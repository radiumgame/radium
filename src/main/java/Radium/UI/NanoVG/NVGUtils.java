// https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/nanovg/NanoVGUtils.java

package Radium.UI.NanoVG;

import Radium.Color;
import Radium.Components.UI.Image;
import Radium.Components.UI.Text;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

public class NVGUtils {

    protected NVGUtils() {}

    public static void Image(Image image) {
        NVGRenderQueue.renderQueue.add(() -> {
            NanoVG.nvgBeginPath(NVG.Instance);
            NanoVG.nvgRect(NVG.Instance, image.position.x, image.position.y, image.size.x, image.size.y);
            NanoVG.nvgFillPaint(NVG.Instance, image.pattern);
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
