package RadiumEditor.Debug.Gizmo;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Physics.Rigidbody;
import Radium.Graphics.Material;
import Radium.Graphics.Mesh;
import Radium.Graphics.Renderers.Renderers;
import Radium.Graphics.Shader;
import Radium.Math.Mathf;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector3;
import Radium.ModelLoader;
import Radium.Physics.ColliderType;
import Radium.Variables;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class ColliderGizmo extends Gizmo {

    private static Vector3 colliderColor = new Vector3(0, 1.0f, 0);

    private Rigidbody rigidbody;
    private Mesh mesh;
    private Material material;

    private Shader shader;

    public ColliderGizmo(Rigidbody rigidbody) {
        this.rigidbody = rigidbody;
        shader = Renderers.renderers.get(0).shader;

        Create();

        GizmoManager.gizmos.add(this);
    }

    private void Create() {
        ColliderType colliderType = rigidbody.collider;
        if (colliderType == ColliderType.Box) {
            mesh = Mesh.Cube(1, 1);
        } else if (colliderType == ColliderType.Sphere) {
            mesh = ModelLoader.LoadModel("EngineAssets/Models/Sphere.fbx", false).GetChildren().get(0).GetComponent(MeshFilter.class).mesh;
        }

        material = new Material("EngineAssets/Textures/Misc/blank.jpg");
    }

    public void UpdateCollider() {
        Create();
    }

    @Override
    public void Update() {
        if (!rigidbody.showCollider) return;

        Render();
    }

    @Override
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
        GL30.glEnableVertexAttribArray(3);

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
        GL30.glDisableVertexAttribArray(3);

        GL30.glBindVertexArray(0);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    private Matrix4f CalculateTransform() {
        Transform transform = rigidbody.gameObject.transform;
        ColliderType colliderType = rigidbody.collider;

        Matrix4f transformMatrix = new Matrix4f().identity();
        transformMatrix.translate(transform.position.x, transform.position.y, transform.position.z);

        transformMatrix.rotateX(Mathf.Radians(transform.rotation.x));
        transformMatrix.rotateY(Mathf.Radians(transform.rotation.y));
        transformMatrix.rotateZ(Mathf.Radians(transform.rotation.z));

        if (colliderType == ColliderType.Box) {
            transformMatrix.scale(transform.scale.x * rigidbody.GetColliderScale().x, transform.scale.y * rigidbody.GetColliderScale().y, transform.scale.z * rigidbody.GetColliderScale().z);
        } else if (colliderType == ColliderType.Sphere) {
            transformMatrix.scale(transform.scale.x * (rigidbody.GetColliderRadius() * 2), transform.scale.y * (rigidbody.GetColliderRadius() * 2), transform.scale.z * (rigidbody.GetColliderRadius() * 2));
        }

        return transformMatrix;
    }

}
