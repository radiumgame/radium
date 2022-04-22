package Radium.Components.UI;

import Radium.*;
import Radium.Input.Input;
import Radium.Math.Vector.Vector2;
import RadiumEditor.Annotations.HideInEditor;
import RadiumEditor.Console;
import RadiumEditor.Viewport;

public class Button extends Component {

    private Image image;
    private boolean lastFrame = false;

    @HideInEditor
    public boolean needToAdd = true;

    @HideInEditor
    public boolean isClicked = false;

    public Button() {
        submenu = "UI";
        impact = PerformanceImpact.Low;

        description = "Clickable image that triggers an event";
        LoadIcon("button.png");
    }

    public void Update() {
        if (Input.GetMouseButton(0) && !lastFrame) {
            if (Application.Editor) {
                Vector2 mouse = Input.GetMousePosition();
                float x = InverseLerp(Viewport.position.x, Viewport.position.x + Viewport.size.x, 0, 1920, mouse.x);
                float y = InverseLerp(Viewport.position.y, Viewport.position.y + Viewport.size.y, 1080, 0,mouse.y);

                Vector2 pos = (Vector2)image.mesh.Position.clone();
                Vector2 size = (Vector2)image.mesh.Size.clone();
                pos.x -= size.x / 2;

                if (x >= pos.x && x <= pos.x + size.x && y >= pos.y && y <= pos.y + size.y) {
                    isClicked = true;
                }
            } else {
                Vector2 mouse = Input.GetMousePosition();
                float x = InverseLerp(0, Window.width, 0, 1920, mouse.x);
                float y = InverseLerp(0,  Window.height, 1080, 0,mouse.y);

                Vector2 pos = (Vector2)image.mesh.Position.clone();
                Vector2 size = (Vector2)image.mesh.Size.clone();
                pos.x -= size.x / 2;

                if (x >= pos.x && x <= pos.x + size.x && y >= pos.y && y <= pos.y + size.y) {
                    isClicked = true;
                }
            }

            lastFrame = true;
        } else if (!Input.GetMouseButton(0) && lastFrame) {
            lastFrame = false;
        }

        if (!Input.GetMouseButton(0) && isClicked) {
            isClicked = false;
        }
    }

    public void OnAdd() {
        if (!needToAdd) return;

        if (!gameObject.ContainsComponent(Text.class)) {
            Text text = (Text)gameObject.AddComponent(new Text("Text"));
            text.Position = new Vector2(880, 480);
            text.color = new Color(0, 0, 0);
            text.UpdateTransform();
        }
        if (!gameObject.ContainsComponent(Image.class)) {
            image = (Image)gameObject.AddComponent(new Image());
            image.mesh.Position = new Vector2(960, 500);
            image.mesh.Size = new Vector2(400, 100);
        }
    }

    private static float InverseLerp(float imin, float imax, float omin, float omax, float v) {
        float t = (v - imin) / (imax - imin);
        float lerp = (1f - t) * omin + omax * t;

        return lerp;
    }

}
