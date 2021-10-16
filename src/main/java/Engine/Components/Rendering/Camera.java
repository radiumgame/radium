package Engine.Components.Rendering;

import Engine.Component;
import Engine.Gizmo.ComponentGizmo;
import Engine.Gizmo.GizmoManager;
import Engine.Graphics.Texture;
import Engine.PerformanceImpact;
import Engine.Variables;
import Engine.Window;
import org.joml.Matrix4f;

public class Camera extends Component {

    private transient Matrix4f projection;

    public float fov = 70f;
    public float near = 0.1f;
    public float far = 100f;

    private transient ComponentGizmo gizmo;

    public Camera() {
        icon = new Texture("EngineAssets/Editor/Icons/camera.png").textureID;
        Variables.DefaultCamera = this;

        RunInEditMode = true;
        description = "A simulated camera that can be moved throughout a scene";
        impact = PerformanceImpact.Low;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        if (gizmo == null && gameObject != null) {
            gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/camera.png"));
        }

        CalculateProjection();

        if (Variables.DefaultCamera == null) Variables.DefaultCamera = this;
    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {
        gizmo.Destroy();
    }

    @Override
    public void OnVariableUpdate() {

    }

    @Override
    public void GUIRender() {

    }

    public void CalculateProjection() {
        float aspect = (float)Window.width / (float)Window.height;
        projection = new Matrix4f().perspective((float)Math.toRadians(fov), aspect, near, far);
    }

    public Matrix4f GetProjection() {
        return projection;
    }

}
