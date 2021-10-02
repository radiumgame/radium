package Engine.Graphics;

import Engine.Util.NonInstantiatable;
import Engine.Variables;

public final class Lighting extends NonInstantiatable {

    private static Shader shader;

    private static float ambient = 0.4f;

    public static void Initialize() {
        shader = Variables.LitRenderer.shader;
    }

    public static void UpdateUniforms() {
        shader.Bind();

        shader.SetUniform("ambient", ambient);

        shader.Unbind();
    }

}
