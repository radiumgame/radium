package Engine.Graphics;

import Engine.Graphics.Renderers.Renderers;
import Engine.Util.NonInstantiatable;

public final class Lighting extends NonInstantiatable {

    private static Shader shader;

    private static float ambient = 0.5f;

    public static void Initialize() {
        shader = Renderers.renderers.get(1).shader;
    }

    public static void UpdateUniforms() {
        shader.Bind();

        shader.SetUniform("ambient", ambient);

        shader.Unbind();
    }

}
