package Radium.Engine.Components.UI;

import Radium.Engine.Color.Color;
import Radium.Engine.Component;
import Radium.Engine.PerformanceImpact;
import Radium.Engine.UI.NanoVG.NVGUtils;

public class Panel extends Component {

    public Color color = new Color(255, 255, 255, 255);
    public int layerOrder;

    public Panel() {
        submenu = "UI";
        name = "Panel";

        description = "A simple panel that is covering the entire screen";
        impact = PerformanceImpact.Low;

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
