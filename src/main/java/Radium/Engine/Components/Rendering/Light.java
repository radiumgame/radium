package Radium.Engine.Components.Rendering;

import Radium.Editor.Console;
import Radium.Engine.Color.Color;
import Radium.Engine.Component;
import Radium.Engine.Graphics.Framebuffer.DepthFramebuffer;
import Radium.Engine.Graphics.Lighting.LightType;
import Radium.Engine.Graphics.RenderQueue;
import Radium.Engine.Graphics.Shadows.ShadowCubemap;
import Radium.Engine.Graphics.Shadows.Shadows;
import Radium.Engine.Math.Mathf;
import Radium.Engine.Math.Transform;
import Radium.Engine.SceneManagement.Scene;
import Radium.Engine.SceneManagement.SceneManager;
import Radium.Editor.Annotations.HideInEditor;
import Radium.Editor.Annotations.RunInEditMode;
import Radium.Editor.Debug.Gizmo.ComponentGizmo;
import Radium.Engine.Graphics.Renderers.Renderers;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.PerformanceImpact;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@RunInEditMode
public class Light extends Component {

    public static int LightIndex = 0;
    @HideInEditor
    public int index;

    public static List<Light> lightsInScene = new ArrayList<>();
    private final transient Shader shader;

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
    public float shadowDistance = 25f;

    public LightType lightType = LightType.Point;

    private transient ComponentGizmo gizmo;
    private transient Matrix4f lightSpace;
    public transient Matrix4f[] pointLightSpace = new Matrix4f[6];

    public transient DepthFramebuffer shadowFramebuffer;
    public transient ShadowCubemap shadowCubemap;
    private transient Transform lastTransform;

    /**
     * Create empty light component
     */
    public Light() {
        icon = new Texture("EngineAssets/Editor/Icons/light.png").textureID;
        description = "Simulated light using shaders";
        impact = PerformanceImpact.Medium;

        shader = Renderers.renderers.get(1).shader;
        submenu = "Rendering";

        shadowFramebuffer = new DepthFramebuffer(Shadows.ShadowFramebufferSize, Shadows.ShadowFramebufferSize);
        shadowCubemap = new ShadowCubemap();
    }

    public void Start() {

    }
    
    public void Update() {
        if (!lastTransform.equals(gameObject.transform)) {
            lastTransform = gameObject.transform.Clone();
            UpdateUniforms();
            CalculateLightSpace();
        }
    }

    
    public void Stop() {

    }

    
    public void OnAdd() {
        lightsInScene.add(this);
        lastTransform = gameObject.transform.Clone();

        index = LightIndex;
        LightIndex++;
        gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/light.png"));

        UpdateUniforms();
        CalculateAllLightSpace();
    }

    public void OnRemove() {
        gizmo.Destroy();

        shader.Bind();

        shader.SetUniform("lights[" + index + "].position", Vector3.Zero());
        shader.SetUniform("lights[" + index + "].color", Vector3.Zero());
        shader.SetUniform("lights[" + index + "].intensity", 0);
        shader.SetUniform("lights[" + index + "].attenuation", 0);
        shader.SetUniform("lights[" + index + "].farPlane", 0);
        shader.SetUniform("lights[" + index + "].lightType", 0);
        shader.SetUniform("lightSpace", lightSpace);

        shader.Unbind();

        shadowCubemap.Destroy();
        for (Light light : lightsInScene) {
            light.OnLightRemoved(index);
        }
        lightsInScene.remove(this);
        LightIndex--;
    }

    public void Init() {
        if (gameObject == null) return;

        UpdateUniforms();
        CalculateAllLightSpace();
    }
    
    public void UpdateVariable(String update) {
        UpdateUniforms();

        if (DidFieldChange(update, "shadowDistance")) {
            CalculateLightSpace();
        } else if (DidFieldChange(update, "lightType")) {
            CalculateLightSpace();
        }
    }
    
    public void GUIRender() {

    }

    public static int currentIndex;
    public void DepthTest() {
        currentIndex = index;

        if (lightType == LightType.Directional) {
            shadowFramebuffer.Bind();
        } else if (lightType == LightType.Point) {
            shadowCubemap.Bind();
        }

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        SceneManager.GetCurrentScene().ShadowRender(lightSpace, this);

        if (lightType == LightType.Directional) {
            shadowFramebuffer.Unbind();
        } else if (lightType == LightType.Point) {
            shadowCubemap.Unbind();
        }
    }

    private void UpdateUniforms() {
        shader.Bind();

        shader.SetUniform("lightSpace", lightSpace);
        shader.SetUniform("lights[" + index + "].position", gameObject.transform.WorldPosition());
        shader.SetUniform("lights[" + index + "].color", color.ToVector3());
        shader.SetUniform("lights[" + index + "].intensity", intensity);
        shader.SetUniform("lights[" + index + "].attenuation", attenuation);
        shader.SetUniform("lights[" + index + "].farPlane", shadowDistance);
        shader.SetUniform("lights[" + index + "].lightType", lightType.ordinal());

        shader.Unbind();
    }

    public void CalculateLightSpace() {
        float near = 0.1f;

        if (lightType == LightType.Directional) {
            Matrix4f projection = new Matrix4f().ortho(-16, 16, -9, 9, near, shadowDistance);
            Matrix4f view = new Matrix4f().lookAt(
                    new Vector3f(gameObject.transform.WorldPosition().x, gameObject.transform.WorldPosition().y, gameObject.transform.WorldPosition().z),
                    new Vector3f(0, 0, 0),
                    new Vector3f(0, 1, 0));

            lightSpace = projection.mul(view);
        }
        else if (lightType == LightType.Point) {
            Vector3 p = gameObject.transform.WorldPosition();
            Vector3f pos = new Vector3f(p.x, p.y, p.z);
            Matrix4f projection = new Matrix4f().perspective(Mathf.Radians(90), 1, near, shadowDistance);

            pointLightSpace[0] = new Matrix4f(projection).mul(new Matrix4f().lookAt(pos, new Vector3f(pos.x + 1, pos.y, pos.z), new Vector3f(0, -1, 0)));
            pointLightSpace[1] = new Matrix4f(projection).mul(new Matrix4f().lookAt(pos, new Vector3f(pos.x - 1, pos.y, pos.z), new Vector3f(0, -1, 0)));
            pointLightSpace[2] = new Matrix4f(projection).mul(new Matrix4f().lookAt(pos, new Vector3f(pos.x, pos.y + 1, pos.z), new Vector3f(0, 0, 1)));
            pointLightSpace[3] = new Matrix4f(projection).mul(new Matrix4f().lookAt(pos, new Vector3f(pos.x, pos.y - 1, pos.z), new Vector3f(0, 0, -1)));
            pointLightSpace[4] = new Matrix4f(projection).mul(new Matrix4f().lookAt(pos, new Vector3f(pos.x, pos.y, pos.z + 1), new Vector3f(0, -1, 0)));
            pointLightSpace[5] = new Matrix4f(projection).mul(new Matrix4f().lookAt(pos, new Vector3f(pos.x, pos.y, pos.z - 1), new Vector3f(0, -1, 0)));
        }
    }

    private void CalculateAllLightSpace() {
        LightType originalLightType = lightType;
        lightType = LightType.Directional;
        CalculateLightSpace();
        lightType = LightType.Point;
        CalculateLightSpace();
        lightType = originalLightType;
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
