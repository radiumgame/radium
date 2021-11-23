package Engine.Components.Rendering;

import Editor.Console;
import Engine.Color;
import Engine.Component;
import Engine.Debug.Gizmo.ComponentGizmo;
import Engine.Graphics.Renderers.Renderers;
import Engine.Graphics.Shader;
import Engine.Graphics.Texture;
import Engine.Math.Mathf;
import Engine.Math.Matrix4;
import Engine.Math.Vector.Vector3;
import Engine.PerformanceImpact;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Light extends Component {

    public static int LightIndex = 0;
    private int index;

    private static List<Light> lightsInScene = new ArrayList<>();

    private Shader shader;

    public Color color = new Color(255, 255, 255);
    public float intensity = 1f;
    public float attenuation = 0.045f;

    private transient ComponentGizmo gizmo;
    private transient Matrix4f lightSpace;

    public Light() {
        icon = new Texture("EngineAssets/Editor/Icons/light.png").textureID;
        RunInEditMode = true;
        description = "Simulated light using shaders";
        impact = PerformanceImpact.Medium;

        index = LightIndex;
        LightIndex++;

        shader = Renderers.renderers.get(1).shader;
        lightsInScene.add(this);
        submenu = "Rendering";
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        CalculateLightSpace();
        UpdateUniforms();
    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {
        gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/light.png"));
    }

    @Override
    public void OnRemove() {
        shader.Bind();

        shader.SetUniform("lights[" + index + "].position", Vector3.Zero);
        shader.SetUniform("lights[" + index + "].color", Vector3.Zero);
        shader.SetUniform("lights[" + index + "].intensity", 0);
        shader.SetUniform("lights[" + index + "].attenuation", 0);

        gizmo.Destroy();

        shader.Unbind();

        for (Light light : lightsInScene) {
            light.OnLightRemoved(index);
        }
    }

    @Override
    public void UpdateVariable() {

    }

    @Override
    public void GUIRender() {

    }

    private void UpdateUniforms() {
        shader.Bind();

        shader.SetUniform("lightSpace", lightSpace);
        shader.SetUniform("lights[" + index + "].position", gameObject.transform.position);
        shader.SetUniform("lights[" + index + "].color", color.ToVector3());
        shader.SetUniform("lights[" + index + "].intensity", intensity);
        shader.SetUniform("lights[" + index + "].attenuation", attenuation);

        shader.Unbind();
    }

    private void CalculateLightSpace() {
        float near = 0.1f;
        float far = 25f;
        Matrix4f projection = new Matrix4f().ortho(-16, 16, -9, 9, near, far);
        Matrix4f view = new Matrix4f().lookAt(
                new Vector3f(gameObject.transform.position.x, gameObject.transform.position.y, gameObject.transform.position.z),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 1, 0));
        lightSpace = projection.mul(view);
    }

    public void OnLightRemoved(int lightIndex) {
        if (lightIndex < index) {
            index--;
        }
    }

}
