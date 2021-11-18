package Engine.Graphics;

import Engine.Components.Rendering.Light;
import Engine.Graphics.Renderers.Renderers;
import Engine.Util.NonInstantiatable;

public final class Lighting extends NonInstantiatable {

    private static Shader shader;

    private static float ambient = 0.3f;
    private static boolean useBlinn = true;

    public static void Initialize() {
        shader = Renderers.renderers.get(1).shader;
    }

    public static void UpdateUniforms() {
        shader.Bind();

        shader.SetUniform("lightCount", Light.LightIndex + 1);
        shader.SetUniform("ambient", ambient);
        shader.SetUniform("useBlinn", useBlinn);

        shader.Unbind();
    }

}
