package Radium.Engine.Components.UI;

import Radium.Build;
import Radium.Editor.Annotations.RangeFloat;
import Radium.Editor.Console;
import Radium.Engine.*;
import Radium.Engine.Color.Color;
import Radium.Engine.Input.Input;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Editor.Annotations.HideInEditor;
import Radium.Editor.Viewport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.List;

public class Button extends Component {

    private Image image;

    @HideInEditor
    public boolean needToAdd = true;

    @HideInEditor
    public boolean isClicked = false;

    @JsonIgnore
    public PythonInterpreter interpreter;
    @JsonIgnore
    public PyObject buttonPy = null;

    @RangeFloat(min = 0, max = 100)
    public float dimAmount = 15;
    private boolean dimmed = false;

    public Button() {
        submenu = "UI";
        impact = PerformanceImpact.Low;

        description = "Clickable image that triggers an event";
        LoadIcon("button.png");
    }

    public void Update() {
        if (image == null) {
            image = gameObject.GetComponent(Image.class);
            if (image == null) {
                image = (Image)gameObject.AddComponent(new Image());
            }
        }

        boolean isHovering = false;
        if (Application.Editor) {
            Vector2 mouse = Input.GetMousePosition();
            float x = InverseLerp(Viewport.position.x + Viewport.imagePosition.x, Viewport.position.x + Viewport.imagePosition.x + Viewport.imageSize.x, 0, 1920, mouse.x);
            float y = InverseLerp(Viewport.position.y + Viewport.imagePosition.y, Viewport.position.y + Viewport.imagePosition.y + Viewport.imageSize.y, 1080, 0,mouse.y);
            y = 1080 - y;

            Vector2 pos = (Vector2)image.position.clone();
            Vector2 size = (Vector2)image.size.clone();

            isHovering = IsHovering(pos, size, new Vector2(x, y));
        } else {
            Vector2 mouse = Input.GetMousePosition();
            float x = InverseLerp(0, Window.width, 0, 1920, mouse.x);
            float y = InverseLerp(0,  Window.height, 1080, 0,mouse.y);
            y = 1080 - y;

            Vector2 pos = (Vector2)image.position.clone();
            Vector2 size = (Vector2)image.size.clone();

            isHovering = IsHovering(pos, size, new Vector2(x, y));
        }

        if (Input.GetMouseButtonPressed(0) && isHovering) {
            isClicked = true;
            image.color.r -= dimAmount / 100;
            image.color.g -= dimAmount / 100;
            image.color.b -= dimAmount / 100;
            dimmed = true;
        } else {
            isClicked = false;
        }

        if (Input.GetMouseButtonReleased(0) && dimmed) {
            image.color.r += dimAmount / 100;
            image.color.g += dimAmount / 100;
            image.color.b += dimAmount / 100;
            dimmed = false;
        }

        if (isClicked && buttonPy != null) {
            buttonPy.__getattr__("callback").__call__(interpreter.get("ButtonEvent").__call__());
        }
    }

    private boolean IsHovering(Vector2 pos, Vector2 size, Vector2 mouse) {
        return mouse.x >= pos.x && mouse.x <= pos.x + size.x && mouse.y >= pos.y && mouse.y <= pos.y + size.y;
    }

    public void OnAdd() {
        if (!needToAdd) return;

        if (!gameObject.ContainsComponent(Text.class)) {
            Text text = (Text)gameObject.AddComponent(new Text("Text"));
            text.Position = new Vector2(880, 480);
            text.color = new Color(0, 0, 0);
            text.layerOrder = 1;
        }
        if (!gameObject.ContainsComponent(Image.class)) {
            image = (Image)gameObject.AddComponent(new Image());
            image.position = new Vector2(760, 410);
            image.size = new Vector2(400, 100);
        }
    }

    private static float InverseLerp(float imin, float imax, float omin, float omax, float v) {
        float t = (v - imin) / (imax - imin);
        float lerp = (1f - t) * omin + omax * t;

        return lerp;
    }

}
