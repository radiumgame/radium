package Radium.Components.UI;

import Radium.Color;
import Radium.Component;
import Radium.UI.NanoVG.NVGUtils;

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
