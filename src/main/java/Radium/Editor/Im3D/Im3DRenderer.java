package Radium.Editor.Im3D;

import Radium.Engine.Components.Rendering.Camera;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Mathf;
import Radium.Engine.Math.Matrix4;
import Radium.Engine.Math.Transform;
import Radium.Engine.Math.Vector.Vector3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class Im3DRenderer {

    private Camera camera;

    private Shader shader;
    public Transform objectTransform;

    private static final String vs = """
            #version 330 core
            layout(location = 0) in vec3 position;
            layout(location = 1) in vec2 texCoord;
            layout(location = 2) in vec3 normal;
            layout(location = 3) in vec3 tangent;
            layout(location = 4) in vec3 bitangent;
            uniform mat4 model;
            uniform mat4 view;
            uniform mat4 projection;
            out vec2 uv;
            void main() {
                uv = texCoord;
                
                vec4 worldPosition = model * vec4(position, 1);
                gl_Position = projection * view * worldPosition;
            }""";
    private static final String fs = """
            #version 330 core
            in vec2 uv;
            out vec4 frag;
            uniform sampler2D tex;
            uniform vec3 color;
            void main() {
                frag = texture(tex, uv) * vec4(color, 1);
            }""";

    public Im3DRenderer() {
        this.shader = Shader.Load(vs, fs);

        objectTransform = new Transform();
        objectTransform.position = new Vector3(0, 0, -3);
        objectTransform.rotation = Vector3.Zero();
        objectTransform.scale = Vector3.One();
    }

    private final Transform empty = new Transform();
    private final Matrix4f projection = new Matrix4f().identity().perspective(Mathf.Radians(90), 16.0f / 9.0f, 0.01f, 100f);
    public void Render(Mesh mesh, Texture texture) {
        if (mesh == null) return;

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL30.glBindVertexArray(mesh.GetVAO());

        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);
        GL30.glEnableVertexAttribArray(4);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, mesh.GetIBO());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureID);

        shader.Bind();

        shader.SetUniform("model", Matrix4.Transform(objectTransform, false));
        shader.SetUniform("view", Matrix4.View(empty));
        shader.SetUniform("projection", projection);
        shader.SetUniform("color", new Vector3(0.7f, 0.7f, 0.7f));

        shader.SetUniform("tex", 0);

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.GetIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.Unbind();

        GL13.glActiveTexture(0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glDisableVertexAttribArray(3);
        GL30.glDisableVertexAttribArray(4);

        GL30.glBindVertexArray(0);
    }

}
