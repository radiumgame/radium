package Engine.Objects;

import Engine.Graphics.Mesh;
import Engine.Math.Transform;

public class EditorObject {

    public Transform transform;
    public Mesh mesh;

    public EditorObject(Transform transform, Mesh mesh) {
        this.transform = transform;
        this.mesh = mesh;
    }

}
