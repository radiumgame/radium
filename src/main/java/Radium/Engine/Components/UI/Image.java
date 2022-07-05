package Radium.Engine.Components.UI;

import Radium.Engine.Color.Color;
import Radium.Engine.Component;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.UI.NanoVG.NVG;
import Radium.Engine.UI.NanoVG.NVGUtils;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

public class Image extends Component {

    public Vector2 position = new Vector2(0, 0);
    public Vector2 size = new Vector2(100, 100);
    public Texture texture = new Texture();
    public Color color = new Color(255, 255, 255, 255);
    public int layerOrder;

    public NVGPaint pattern;
    private int currentTex = 0;

    /**
     * Create empty image component
     */
    public Image() {
        LoadIcon("image.png");
        submenu = "UI";
    }

    
    public void Start() {

    }

    
    public void Update() {
        NVGUtils.Image(this);
    }

    public void EditorUpdate() {
        int[] width = new int[1];
        int[] height = new int[1];
        NanoVG.nvgImageSize(NVG.Instance, currentTex, width, height);
        pattern = NanoVG.nvgImagePattern(NVG.Instance, position.x, position.y, width[0], height[0], 0, currentTex, 1.0f, pattern);

        order = layerOrder;
    }

    public void Stop() {

    }

    
    public void OnAdd() {
        CreatePattern();
    }

    
    public void OnRemove() {

    }

    
    public void UpdateVariable(String update) {
        if (DidFieldChange(update, "texture")) {
            CreatePattern();
        }
    }

    private void CreatePattern() {
        currentTex = NanoVG.nvgCreateImage(NVG.Instance, texture.filepath, NanoVG.NVG_IMAGE_NEAREST);
        int[] width = new int[1];
        int[] height = new int[1];
        NanoVG.nvgImageSize(NVG.Instance, currentTex, width, height);

        pattern = NVGPaint.create();
        pattern = NanoVG.nvgImagePattern(NVG.Instance, position.x, position.y, width[0], height[0], 0, currentTex, 1.0f, pattern);
    }

}
