package Radium.Editor.Debug.Gizmo;

import Radium.Engine.Graphics.*;
import Radium.Engine.Graphics.Renderers.EditorRenderer;
import Radium.Engine.Math.Mathf;
import Radium.Engine.Math.Matrix4;
import Radium.Engine.Math.Transform;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.EditorObject;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Variables;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

/**
 * Gizmo that shows texture of gizmo icon
 */
public class ComponentGizmo extends Gizmo {

    /**
     * Gizmo component game obejct
     */
    public GameObject gameObject;

    /**
     * Gizmo component icon
     */
    public Texture texture;

    private EditorObject editorObject;
    private Transform transform;

    private boolean isAlive = true;

    /**
     * Creates mesh off of game object and its texture
     * @param gameObject Game object of component
     * @param texture Component icon
     */
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
        model.rotate(Mathf.Radians(editorObject.transform.rotation.z), new Vector3f(0, 0, 1));
        model.scale(1, 1, 1);

        GL11.glDepthMask(false);
        EditorRenderer.Render(editorObject, model, view);
        GL11.glDepthMask(true);
    }

    
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
