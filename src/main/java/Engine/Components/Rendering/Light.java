package Engine.Components.Rendering;

import Editor.Console;
import Engine.Color;
import Engine.Component;
import Engine.Gizmo.ComponentGizmo;
import Engine.Gizmo.GizmoManager;
import Engine.Graphics.Renderers.Renderers;
import Engine.Graphics.Shader;
import Engine.Graphics.Texture;
import Engine.Math.Vector.Vector3;
import Engine.Variables;

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

    public Light() {
        icon = new Texture("EngineAssets/Editor/Icons/light.png").textureID;
        RunInEditMode = true;

        index = LightIndex;
        LightIndex++;

        shader = Renderers.renderers.get(1).shader;
        lightsInScene.add(this);
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        if (gizmo == null && gameObject != null) {
            gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/light.png"));
        }

        UpdateUniforms();
    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {
        shader.Bind();

        shader.SetUniform("lights[" + index + "].position", Vector3.Zero);
        shader.SetUniform("lights[" + index + "].color", Vector3.Zero);
        shader.SetUniform("lights[" + index + "].intensity", 0);
        shader.SetUniform("lights[" + index + "].attenuation", 0);

        GizmoManager.gizmos.remove(gizmo);

        shader.Unbind();

        for (Light light : lightsInScene) {
            light.OnLightRemoved(index);
        }
    }

    @Override
    public void OnVariableUpdate() {

    }

    @Override
    public void GUIRender() {

    }

    private void UpdateUniforms() {
        shader.Bind();

        shader.SetUniform("lights[" + index + "].position", gameObject.transform.position);
        shader.SetUniform("lights[" + index + "].color", color.ToVector3());
        shader.SetUniform("lights[" + index + "].intensity", intensity);
        shader.SetUniform("lights[" + index + "].attenuation", attenuation);

        shader.Unbind();
    }

    public void OnLightRemoved(int lightIndex) {
        if (lightIndex < index) {
            index--;
        }
    }

}
