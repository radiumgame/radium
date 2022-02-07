package RadiumEditor.Debug.Gizmo;

import Radium.Graphics.*;
import Radium.Graphics.Renderers.EditorRenderer;
import Radium.Math.Mathf;
import Radium.Math.Matrix4;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Objects.EditorObject;
import Radium.Objects.GameObject;
import Radium.Variables;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class ComponentGizmo extends Gizmo {

    public GameObject gameObject;
    public Texture texture;

    private EditorObject editorObject;
    private Transform transform;

    private boolean isAlive = true;

    public ComponentGizmo(GameObject gameObject, Texture texture) {
        this.gameObject = gameObject;
        this.texture = texture;

        Create();

        GizmoManager.gizmos.add(this);
    }

    private void Create() {
        Mesh mesh = Mesh();

        transform = new Transform();
        transform.position = gameObject.transform.position;
        transform.rotation = new Vector3(0, 0, 0);
        transform.scale = Vector3.One();
        editorObject = new EditorObject(transform, mesh, new Material(texture.filepath));
    }

    @Override
    public void Update() {
        if (!isAlive) return;

        editorObject.transform.position = gameObject.transform.WorldPosition();

        Matrix4f model = new Matrix4f();
        Matrix4f view = Matrix4.View(Variables.EditorCamera.transform);
        float x = editorObject.transform.position.x;
        float y = editorObject.transform.position.y;
        float z = editorObject.transform.position.z;
        model.translate(x, y, z);
        model.m00(view.m00());
        model.m01(view.m10());
        model.m02(view.m20());
        model.m10(view.m01());
        model.m11(view.m11());
        model.m12(view.m21());
        model.m20(view.m02());
        model.m21(view.m12());
        model.m22(view.m22());
        model.rotate(Mathf.Radians(editorObject.transform.WorldRotation().z), new Vector3f(0, 0, 1));
        model.scale(1, 1, 1);

        GL11.glDepthMask(false);
        EditorRenderer.Render(editorObject, model, view);
        GL11.glDepthMask(true);
    }

    @Override
    public void OnDestroy() {
        editorObject.mesh.Destroy();
        isAlive = false;
    }

    private Mesh Mesh() {
        Vertex[] vertices = new Vertex[]{
                new Vertex(new Vector3(-0.5f, 0.5f, 0), new Vector3(0, 0, 1), new Vector2(0, 0)),
                new Vertex(new Vector3(-0.5f, -0.5f, 0), new Vector3(0, 0, 1), new Vector2(0, 1)),
                new Vertex(new Vector3(0.5f, -0.5f, 0), new Vector3(0, 0, 1), new Vector2(1, 1)),
                new Vertex(new Vector3(0.5f, 0.5f, 0), new Vector3(0, 0, 1), new Vector2(1, 0)),
        };
        int[] indices = new int[] {
            0, 1, 3, 3, 1, 2
        };

        return new Mesh(vertices, indices);
    }

}
