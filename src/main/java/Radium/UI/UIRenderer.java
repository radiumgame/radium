package Radium.UI;

import Radium.Graphics.Shader;
import Radium.Math.Mathf;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class UIRenderer {

    private static Shader shader;

    protected UIRenderer() {}

    public static void Initialize() {
        shader = new Shader("EngineAssets/Shaders/UI/vert.glsl", "EngineAssets/Shaders/UI/frag.glsl");
    }

    public static void Render(UIMesh mesh, Transform transform) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, mesh.texture.textureID);

        shader.Bind();

        shader.SetUniform("model", Model(transform));

        shader.SetUniform("color", mesh.color.ToVector3());
        shader.SetUniform("alpha", mesh.color.a);
        shader.SetUniform("tex", 0);

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);

        GL11.glDisable(GL11.GL_BLEND);
    }

    private static Matrix4f Model(Transform transform) {
        Vector3 worldPosition = transform.WorldPosition();
        Vector3 worldRotation = transform.WorldRotation();
        Vector3 worldScale = transform.WorldScale();

        Matrix4f transformMatrix = new Matrix4f().identity();
        transformMatrix.translate(worldPosition.x, worldPosition.y, 0);
        transformMatrix.rotateX(Mathf.Radians(worldRotation.x));
        transformMatrix.rotateY(Mathf.Radians(worldRotation.y));
        transformMatrix.rotateZ(Mathf.Radians(worldRotation.z));
        transformMatrix.scale(worldScale.x, worldScale.y, 1);

        return transformMatrix;
    }

}
