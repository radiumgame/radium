package Radium.Engine.Components.UI;

import Radium.Engine.Color.Color;
import Radium.Engine.Component;
import Radium.Engine.UI.NanoVG.NVGUtils;

public class Panel extends Component {

    public Color color = new Color(255, 255, 255, 255);
    public int layerOrder;

    public Panel() {
        submenu = "UI";
        name = "Panel";

        LoadIcon("panel.png");
    }

    @Override
    public void EditorUpdate() {
        order = layerOrder;
    }

    public void Update() {
        NVGUtils.Panel(this);
    }

}
