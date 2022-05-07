package Radium.Components.Rendering;

import Radium.Color;
import Radium.Component;
import Radium.Graphics.Lighting.LightType;
import RadiumEditor.Annotations.HideInEditor;
import RadiumEditor.Annotations.RunInEditMode;
import RadiumEditor.Debug.Gizmo.ComponentGizmo;
import Radium.Graphics.Renderers.Renderers;
import Radium.Graphics.Shader.Shader;
import Radium.Graphics.Texture;
import Radium.Math.Vector.Vector3;
import Radium.PerformanceImpact;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@RunInEditMode
public class Light extends Component {

    public static int LightIndex = 0;
    @HideInEditor
    public int index;

    public static List<Light> lightsInScene = new ArrayList<>();
    private transient Shader shader;

    /**
     * Color of the light
     */
    public Color color = new Color(255, 255, 255);
    /**
     * Intensity of the light
     */
    public float intensity = 1f;
    /**
     * Attenuation of the light
     */
    public float attenuation = 0.045f;

    public LightType lightType = LightType.Point;

    private transient ComponentGizmo gizmo;
    private transient Matrix4f lightSpace;

    /**
     * Create empty light component
     */
    public Light() {
        icon = new Texture("EngineAssets/Editor/Icons/light.png").textureID;
        description = "Simulated light using shaders";
        impact = PerformanceImpact.Medium;

        shader = Renderers.renderers.get(1).shader;
        lightsInScene.add(this);
        submenu = "Rendering";
    }

    
    public void Start() {

    }

    
    public void Update() {
        CalculateLightSpace();
        UpdateUniforms();
    }

    
    public void Stop() {

    }

    
    public void OnAdd() {
        index = LightIndex;
        LightIndex++;
        gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/light.png"));
    }

    
    public void OnRemove() {
        gizmo.Destroy();

        shader.Bind();

        shader.SetUniform("lights[" + index + "].position", Vector3.Zero());
        shader.SetUniform("lights[" + index + "].color", Vector3.Zero());
        shader.SetUniform("lights[" + index + "].intensity", 0);
        shader.SetUniform("lights[" + index + "].attenuation", 0);
        shader.SetUniform("lights[" + index + "].lightType", 0);

        shader.Unbind();

        for (Light light : lightsInScene) {
            light.OnLightRemoved(index);
        }
        lightsInScene.remove(this);
        LightIndex--;
    }

    
    public void UpdateVariable(String update) {

    }

    
    public void GUIRender() {

    }

    private void UpdateUniforms() {
        shader.Bind();

        shader.SetUniform("lightSpace", lightSpace);
        shader.SetUniform("lights[" + index + "].position", gameObject.transform.WorldPosition());
        shader.SetUniform("lights[" + index + "].color", color.ToVector3());
        shader.SetUniform("lights[" + index + "].intensity", intensity);
        shader.SetUniform("lights[" + index + "].attenuation", attenuation);
        shader.SetUniform("lights[" + index + "].lightType", lightType.ordinal());

        shader.Unbind();
    }

    private void CalculateLightSpace() {
        float near = 0.1f;
        float far = 25f;
        Matrix4f projection = new Matrix4f().ortho(-16, 16, -9, 9, near, far);
        Matrix4f view = new Matrix4f().lookAt(
                new Vector3f(gameObject.transform.WorldPosition().x, gameObject.transform.WorldPosition().y, gameObject.transform.WorldPosition().z),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 1, 0));

        lightSpace = projection.mul(view);
    }

    /**
     * If light is removed, it will recalculate the lights index in the array
     * @param lightIndex The light index that was removed
     */
    public void OnLightRemoved(int lightIndex) {
        if (lightIndex < index) {
            index--;
        }
    }

}
