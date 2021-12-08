package Radium.Components.Misc;

import Radium.Component;
import Radium.Graphics.Texture;
import Radium.Math.Axis;
import Radium.PerformanceImpact;
import Radium.Time;

public class Rotator extends Component {

    public float rotationSpeed = 20f;
    public Axis rotationAxis = Axis.X;

    public Rotator() {
        impact = PerformanceImpact.Low;
        description = "Rotates a GameObject a specified amount of degrees every second";
        icon = new Texture("EngineAssets/Editor/Icons/rotator.png").textureID;
        submenu = "Miscellaneous";
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        if (rotationAxis == Axis.X) {
            gameObject.transform.rotation.x += rotationSpeed * Time.deltaTime;
        } else if (rotationAxis == Axis.Y) {
            gameObject.transform.rotation.y += rotationSpeed * Time.deltaTime;
        } else if (rotationAxis == Axis.Z) {
            gameObject.transform.rotation.z += rotationSpeed * Time.deltaTime;
        }
    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void UpdateVariable() {

    }

    @Override
    public void GUIRender() {

    }

}
