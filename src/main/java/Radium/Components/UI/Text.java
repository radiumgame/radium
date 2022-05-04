package Radium.Components.UI;

import Radium.Color;
import Radium.Component;
import Radium.Math.Vector.Vector2;
import Radium.UI.NanoVG.NVGUtils;
import Radium.UI.NanoVG.Type.Font;
import Radium.UI.NanoVG.Type.TextAlign;
import RadiumEditor.Annotations.RangeInt;
import org.lwjgl.nanovg.NanoVG;

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
    /**
     * Color of text
     */
    public Color color = new Color(1f, 1f, 1f, 1f);

    public TextAlign textAlign = TextAlign.Left;
    public Font font = Font.Arial;

    /**
     * Create empty text component
     */
    public Text() {
        LoadIcon("text.png");
        submenu = "UI";
    }

    /**
     * Create text component with predefined text
     * @param text The display text
     */
    public Text(String text) {
        LoadIcon("text.png");
        this.text = text;
    }

    
    public void Start() {

    }

    
    public void Update() {
        if (gameObject == null) return;

        NVGUtils.Text(text, Position, font.name(), fontSize, GetAlign(textAlign), color);
    }
    
    public void Stop() {

    }

    
    public void OnAdd() {

    }

    
    public void OnRemove() {

    }

    
    public void UpdateVariable() {

    }

    private int GetAlign(TextAlign align) {
        switch (align) {
            case Left:
                return NanoVG.NVG_ALIGN_LEFT;
            case Center:
                return NanoVG.NVG_ALIGN_CENTER;
            case Right:
                return NanoVG.NVG_ALIGN_RIGHT;
        }

        return NanoVG.NVG_ALIGN_LEFT;
    }

}
