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

    
    public void Start() {

    }

    
    public void Update() {
        if (rotationAxis == Axis.X) {
            gameObject.transform.rotation.x += rotationSpeed * Time.deltaTime;
        } else if (rotationAxis == Axis.Y) {
            gameObject.transform.rotation.y += rotationSpeed * Time.deltaTime;
        } else if (rotationAxis == Axis.Z) {
            gameObject.transform.rotation.z += rotationSpeed * Time.deltaTime;
        }
    }

    
    public void Stop() {

    }

    
    public void OnAdd() {

    }

    
    public void OnRemove() {

    }

    
    public void UpdateVariable() {

    }

    
    public void GUIRender() {

    }

}
