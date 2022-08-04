package Radium.Engine.Objects;

import Radium.Engine.Color.Color;
import Radium.Engine.Graphics.Material;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Math.Transform;

/**
 * Objects only visible in edit mode
 */
public class EditorObject {

    /**
     * Transform of editor object
     */
    public Transform transform;
    /**
     * Mesh of object
     */
    public Mesh mesh;
    /**
     * Material for renderer to use
     */
    public Material material;
    public Color color = new Color(1f, 1, 1, 1);

    public boolean enabled = true;

    /**
     * Create an object from predefined parameters
     * @param transform Transform of object
     * @param mesh Mesh of object
     * @param material Material of mesh
     */
    public EditorObject(Transform transform, Mesh mesh, Material material) {
        this.transform = transform;
        this.mesh = mesh;
        this.material = material;
    }

}
