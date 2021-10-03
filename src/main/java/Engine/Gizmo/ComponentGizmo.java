package Engine.Gizmo;

import Engine.Graphics.Mesh;
import Engine.Graphics.Renderers.EditorRenderer;
import Engine.Graphics.Texture;
import Engine.Math.QuaternionUtility;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector3;
import Engine.Objects.EditorObject;
import Engine.Objects.GameObject;
import Engine.Variables;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ComponentGizmo extends Gizmo {

    public GameObject gameObject;
    public Texture texture;

    private EditorObject editorObject;
    private Transform transform;

    public ComponentGizmo(GameObject gameObject, Texture texture) {
        this.gameObject = gameObject;
        this.texture = texture;

        GizmoManager.gizmos.add(this);

        Create();
    }

    private void Create() {
        Mesh mesh = Mesh.Plane(1, 1, texture.filepath);

        transform = new Transform();
        transform.position = gameObject.transform.position;
        transform.rotation = new Vector3(-90, 0, 0);
        transform.scale = Vector3.One;
        editorObject = new EditorObject(transform, mesh);
    }

    @Override
    public void Update() {
        editorObject.transform.position = gameObject.transform.position;
        LookAtEditorCamera();

        EditorRenderer.Render(editorObject);
    }

    private void LookAtEditorCamera() {
        Vector3 difference = Vector3.Subtract(Variables.DefaultCamera.gameObject.transform.position, editorObject.transform.position);
        Quaternionf quaternionRotation = new Quaternionf().lookAlong(new Vector3f(difference.x, difference.y, difference.z), new Vector3f(0, 1, 0));
        Vector3 rotation = QuaternionUtility.GetEuler(quaternionRotation);

        editorObject.transform.rotation.z = rotation.y;
    }

}
