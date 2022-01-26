package Radium.Objects;

import Radium.Graphics.Material;
import Radium.Graphics.Mesh;
import Radium.Math.Transform;

public class EditorObject {

    public Transform transform;
    public Mesh mesh;
    public Material material;

    public EditorObject(Transform transform, Mesh mesh, Material material) {
        this.transform = transform;
        this.mesh = mesh;
        this.material = material;
    }

}
