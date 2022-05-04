// https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/nanovg/NanoVGUtils.java

package Radium.UI.NanoVG;

import Radium.Color;
import Radium.Math.Vector.Vector2;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

public class NVGUtils {

    protected NVGUtils() {}

    public static void Square(Vector2 position, Vector2 size) {
        NVGRenderQueue.renderQueue.add(() -> {
            NanoVG.nvgBeginPath(NVG.Instance);
            NanoVG.nvgRect(NVG.Instance, position.x, position.y, size.x, size.y);
            NanoVG.nvgFillColor(NVG.Instance, CreateColor(Color.Blue()));
            NanoVG.nvgFill(NVG.Instance);
        });
    }

    public static void Text(String text, Vector2 position, String font, float fontSize, int align, Color color) {
        NVGRenderQueue.renderQueue.add(() -> {
            NanoVG.nvgFontSize(NVG.Instance, fontSize);
            NanoVG.nvgFontFace(NVG.Instance, font);
            NanoVG.nvgTextAlign(NVG.Instance, align);
            NanoVG.nvgFillColor(NVG.Instance, CreateColor(color));
            NanoVG.nvgText(NVG.Instance, position.x, position.y, text);
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
