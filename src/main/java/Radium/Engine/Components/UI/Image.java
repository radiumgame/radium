package Radium.Engine.Components.UI;

import Radium.Editor.Annotations.HideInEditor;
import Radium.Editor.Console;
import Radium.Engine.Color.Color;
import Radium.Engine.Component;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.PerformanceImpact;
import Radium.Engine.UI.NanoVG.NVG;
import Radium.Engine.UI.NanoVG.NVGUtils;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;

public class Image extends Component {

    public Vector2 position = new Vector2(0, 0);
    public Vector2 size = new Vector2(100, 100);
    public transient Texture texture = new Texture("EngineAssets/Textures/Misc/blank.jpg", true);
    public Color color = new Color(255, 255, 255, 255);
    public int layerOrder;
    @HideInEditor
    public String texturePath = null;

    public transient NVGPaint pattern;
    private transient int currentTex = 0;

    /**
     * Create empty image component
     */
    public Image() {
        LoadIcon("image.png");
        submenu = "UI";

        description = "Render a 2D image on the screen";
        impact = PerformanceImpact.Low;
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
        CreatePattern(true);
    }

    
    public void OnRemove() {

    }

    
    public void UpdateVariable(String update) {
        if (DidFieldChange(update, "texture")) {
            CreatePattern(false);
        }
    }

    private void CreatePattern(boolean onAdd) {
        if (texturePath != null && !texturePath.equals("") && onAdd) {
            texture = new Texture(texturePath, true);
        }
        texturePath = texture.filepath;

        currentTex = NanoVG.nvgCreateImage(NVG.Instance, texture.filepath, NanoVG.NVG_IMAGE_REPEATX | NanoVG.NVG_IMAGE_REPEATY);
        int[] width = new int[1];
        int[] height = new int[1];
        NanoVG.nvgImageSize(NVG.Instance, currentTex, width, height);

        pattern = NVGPaint.create();
        pattern = NanoVG.nvgImagePattern(NVG.Instance, position.x, position.y, width[0], height[0], 0, currentTex, 1.0f, pattern);
    }

}
