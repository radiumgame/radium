package Radium.Objects;

import Radium.Graphics.Mesh;
import Radium.Math.Transform;

public class EditorObject {

    public Transform transform;
    public Mesh mesh;

    public EditorObject(Transform transform, Mesh mesh) {
        this.transform = transform;
        this.mesh = mesh;
    }

}
