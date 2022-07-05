package Radium.Engine.Components.UI;

import Radium.Engine.Color.Color;
import Radium.Engine.Component;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.UI.NanoVG.NVG;
import Radium.Engine.UI.NanoVG.NVGUtils;
import Radium.Engine.UI.NanoVG.Type.Font;
import Radium.Engine.UI.NanoVG.Type.TextAlign;
import Radium.Editor.Annotations.RangeFloat;
import Radium.Editor.Annotations.RangeInt;
import org.lwjgl.nanovg.NanoVG;
import java.io.File;

public class Text extends Component {

    /**
     * The texts position
     */
    public Vector2 Position = Vector2.Zero();
    /**
     * The display text
     */
    public String text = "Placeholder";
    /**
     * Font size of text
     */
    @RangeInt(min = 1, max = 256)
    public int fontSize = 64;

    @RangeFloat(max = 10)
    public float fontBlur;

    /**
     * Color of text
     */
    public Color color = new Color(1f, 1f, 1f, 1f);

    public TextAlign textAlign = TextAlign.Left;
    public Font font = Font.Arial;
    private String customFont;

    public int layerOrder;

    /**
     * Create empty text component
     */
    public Text() {
        LoadIcon("text.png");
        submenu = "UI";
        order = 1;
    }

    /**
     * Create text component with predefined text
     * @param text The display text
     */
    public Text(String text) {
        LoadIcon("text.png");
        this.text = text;
        order = 1;
    }

    
    public void Start() {

    }

    
    public void Update() {
        if (gameObject == null) return;

        NVGUtils.Text(this);
    }
    
    public void Stop() {

    }

    
    public void OnAdd() {

    }

    
    public void OnRemove() {

    }

    
    public void UpdateVariable(String update) {
        if (DidFieldChange(update, "font")) {
            if (font == Font.Custom) {
                String fontPath = FileExplorer.Choose("ttf");
                if (FileExplorer.IsPathValid(fontPath)) {
                    customFont = fontPath;
                    File f = new File(fontPath);
                    NanoVG.nvgCreateFont(NVG.Instance, f.getName(), fontPath);
                } else {
                    font = Font.Arial;
                    customFont = null;
                }
            }
        }
    }

    @Override
    public void EditorUpdate() {
        order = layerOrder;
    }

    public int GetAlign() {
        switch (textAlign) {
            case Left:
                return NanoVG.NVG_ALIGN_LEFT;
            case Center:
                return NanoVG.NVG_ALIGN_CENTER;
            case Right:
                return NanoVG.NVG_ALIGN_RIGHT;
        }

        return NanoVG.NVG_ALIGN_LEFT;
    }

    public String GetFontName() {
        if (font == Font.Custom) {
            return new File(customFont).getName();
        }

        return font.name();
    }

}
