package Radium.Components.Misc;

import Radium.Component;
import Radium.Graphics.Texture;
import Radium.Math.Axis;
import Radium.PerformanceImpact;
import Radium.Time;

/**
 * Rotates object x degrees a second
 */
public class Rotator extends Component {

    /**
     * Degrees turned in 1 second
     */
    public float rotationSpeed = 20f;
    /**
     * Axis of rotation
     */
    public Axis rotationAxis = Axis.X;

    /**
     * Create empty rotator component
     */
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
