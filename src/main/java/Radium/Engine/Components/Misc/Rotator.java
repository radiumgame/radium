package Radium.Engine.Components.Misc;

import Radium.Engine.Component;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Axis;
import Radium.Engine.PerformanceImpact;
import Radium.Engine.Time;

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
        icon = new Texture("EngineAssets/Editor/Icons/rotator.png", true).GetTextureID();
        submenu = "Miscellaneous";
    }

    
    public void Start() {

    }

    
    public void Update() {
        if (rotationAxis == Axis.X) {
            gameObject.transform.localRotation.x += rotationSpeed * Time.deltaTime;
        } else if (rotationAxis == Axis.Y) {
            gameObject.transform.localRotation.y += rotationSpeed * Time.deltaTime;
        } else if (rotationAxis == Axis.Z) {
            gameObject.transform.localRotation.z += rotationSpeed * Time.deltaTime;
        }
    }

    
    public void Stop() {

    }

    
    public void OnAdd() {

    }

    
    public void OnRemove() {

    }

    
    public void UpdateVariable(String update) {

    }

    
    public void GUIRender() {

    }

}
