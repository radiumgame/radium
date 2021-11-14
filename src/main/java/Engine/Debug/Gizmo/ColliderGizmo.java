package Engine.Debug.Gizmo;

import Engine.Application;
import Engine.Color;
import Engine.Components.Graphics.MeshFilter;
import Engine.Components.Physics.Rigidbody;
import Engine.Graphics.Mesh;
import Engine.Graphics.Renderers.Renderer;
import Engine.Graphics.Renderers.Renderers;
import Engine.Graphics.Shader;
import Engine.Math.Mathf;
import Engine.Math.Matrix4;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector3;
import Engine.ModelLoader;
import Engine.Physics.ColliderType;
import Engine.Variables;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class ColliderGizmo extends Gizmo {

    private static Vector3 colliderColor = new Vector3(0, 1.0f, 0);

    private Rigidbody rigidbody;
    private Mesh mesh;

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
            mesh = Mesh.Cube(1, 1, "EngineAssets/Textures/Misc/blank.jpg");
        } else if (colliderType == ColliderType.Sphere) {
            mesh = ModelLoader.LoadModel("EngineAssets/sphere.fbx", "EngineAssets/Textures/Misc/blank.jpg")[0];
        }
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
        mesh.DestroyMesh();
    }

    private void Render() {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, mesh.GetMaterial().GetTextureID());

        shader.Bind();

        shader.SetUniform("model", CalculateTransform());
        shader.SetUniform("view", Matrix4.View(Variables.EditorCamera.transform));
        shader.SetUniform("projection", Variables.EditorCamera.projection);
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
        } else {
            transformMatrix.scale(transform.scale.x, transform.scale.y, transform.scale.z);
        }

        return transformMatrix;
    }

}
