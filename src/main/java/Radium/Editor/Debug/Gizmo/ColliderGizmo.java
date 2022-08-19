package Radium.Editor.Debug.Gizmo;

import Radium.Editor.Console;
import Radium.Editor.SceneHierarchy;
import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Components.Physics.Rigidbody;
import Radium.Engine.Components.Physics.StaticRigidbody;
import Radium.Engine.Graphics.Material;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Renderers.Renderers;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Math.Mathf;
import Radium.Engine.Math.Transform;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.ModelLoader;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Physics.ColliderType;
import Radium.Engine.Variables;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.par.ParShapes;

/**
 * A gizmo that shows the collider of objects
 */
public class ColliderGizmo extends Gizmo {

    private final static Vector3 colliderColor = new Vector3(0, 1.0f, 0);

    private Rigidbody rigidbody;
    private StaticRigidbody srb;

    private Mesh mesh;
    private Material material;

    private final Shader shader;

    /**
     * Creates collider gizmzo off of rigidbody and collider
     * @param rigidbody Shape rigidbody
     */
    public ColliderGizmo(Rigidbody rigidbody) {
        this.rigidbody = rigidbody;
        shader = Renderers.renderers.get(0).shader;

        Create();

        GizmoManager.gizmos.add(this);
    }

    public ColliderGizmo(StaticRigidbody rigidbody) {
        this.srb = rigidbody;
        shader = Renderers.renderers.get(0).shader;

        Create();

        GizmoManager.gizmos.add(this);
    }

    private void Create() {
        ColliderType colliderType;
        if (rigidbody != null) {
            colliderType = rigidbody.collider;
        } else {
            colliderType = srb.collider;
        }

        if (colliderType == ColliderType.Box) {
            mesh = Mesh.Cube(1, 1);
        } else if (colliderType == ColliderType.Sphere) {
            mesh = Mesh.Sphere(1.01f, 2);
        } else if (colliderType == ColliderType.Mesh) {
            GameObject obj;
            if (rigidbody != null) {
                obj = rigidbody.gameObject;
            } else {
                obj = srb.gameObject;
            }
            MeshFilter mf = obj.GetComponent(MeshFilter.class);
            if (mf == null || mf.mesh == null) {
                Console.Error("Please add a mesh filter or add a mesh");

                if (rigidbody != null)
                    rigidbody.collider = ColliderType.Box;
                else if (srb != null)
                    srb.collider = ColliderType.Box;

                Create();
                return;
            }

            mesh = mf.mesh;
        }

        material = new Material("EngineAssets/Textures/Misc/blank.jpg");
    }

    /**
     * Updates the collider mesh
     */
    public void UpdateCollider() {
        Create();
    }

    
    public void Update() {
        if (rigidbody != null)
            if (!rigidbody.showCollider || SceneHierarchy.current != rigidbody.gameObject) return;
        if (srb != null)
            if (!srb.showCollider || SceneHierarchy.current != srb.gameObject) return;

        Render();
    }

    
    public void OnDestroy() {
        mesh.Destroy();
    }

    private void Render() {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glLineWidth(3.5f);

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, material.GetTextureID());

        shader.Bind();

        shader.SetUniform("model", CalculateTransform());
        shader.SetUniform("view", Variables.EditorCamera.GetView());
        shader.SetUniform("projection", Variables.EditorCamera.GetProjection());
        shader.SetUniform("color", colliderColor);

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);

        GL30.glBindVertexArray(0);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    private Matrix4f CalculateTransform() {
        Transform transform;
        ColliderType colliderType;

        if (rigidbody != null) {
            transform = rigidbody.gameObject.transform;
            colliderType = rigidbody.collider;
        } else {
            transform = srb.gameObject.transform;
            colliderType = srb.collider;
        }

        Matrix4f transformMatrix = new Matrix4f().identity();
        transformMatrix.translate(transform.WorldPosition().x, transform.WorldPosition().y, transform.WorldPosition().z);

        transformMatrix.rotateX(Mathf.Radians(transform.WorldRotation().x));
        transformMatrix.rotateY(Mathf.Radians(transform.WorldRotation().y));
        transformMatrix.rotateZ(Mathf.Radians(transform.WorldRotation().z));

        Vector3 colliderScale;
        float colliderRadius;
        if (rigidbody != null) {
            colliderScale = rigidbody.colliderScale;
            colliderRadius = rigidbody.radius;
        } else {
            colliderScale = srb.colliderScale;
            colliderRadius = srb.radius;
        }

        if (colliderType == ColliderType.Box) {
            transformMatrix.scale(transform.WorldScale().x * colliderScale.x * 2, transform.WorldScale().y * colliderScale.y * 2, transform.WorldScale().z * colliderScale.z * 2);
        } else if (colliderType == ColliderType.Sphere) {
            transformMatrix.scale(transform.WorldScale().x * colliderRadius * 2, transform.WorldScale().y * colliderRadius * 2, transform.WorldScale().z * colliderRadius * 2);
        } else if (colliderType == ColliderType.Mesh) {
            transformMatrix.scale(transform.WorldScale().x, transform.WorldScale().y, transform.WorldScale().z);
        }

        return transformMatrix;
    }

}
