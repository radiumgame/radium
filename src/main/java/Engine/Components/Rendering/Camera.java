package Engine.Components.Rendering;

import Engine.Component;
import Editor.Debug.Gizmo.ComponentGizmo;
import Engine.Graphics.Texture;
import Engine.Math.Mathf;
import Engine.Math.Matrix4;
import Engine.PerformanceImpact;
import Engine.Variables;
import Engine.Window;
import org.joml.Matrix4f;

public class Camera extends Component {

    public float fov = 70f;
    public float near = 0.1f;
    public float far = 100f;

    private transient Matrix4f projection;
    private transient Matrix4f view;
    private transient ComponentGizmo gizmo;

    public Camera() {
        icon = new Texture("EngineAssets/Editor/Icons/camera.png").textureID;
        Variables.DefaultCamera = this;

        RunInEditMode = true;
        description = "A simulated camera that can be moved throughout a scene";
        impact = PerformanceImpact.Low;
        submenu = "Rendering";

        CalculateProjection();
    }

    @Override
    public void Start() {
        CalculateProjection();
    }

    @Override
    public void Update() {
        if (Variables.DefaultCamera == null) Variables.DefaultCamera = this;

        CalculateView();
    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {
        gizmo = new ComponentGizmo(gameObject, new Texture("EngineAssets/Editor/Icons/camera.png"));
        Variables.EditorCamera.CalculateProjection();
    }

    @Override
    public void OnRemove() {
        gizmo.Destroy();
    }

    @Override
    public void UpdateVariable() {
        CalculateProjection();
        Variables.EditorCamera.CalculateProjection();
    }

    @Override
    public void GUIRender() {

    }

    public void CalculateProjection() {
        float aspect = (float)Window.width / (float)Window.height;
        projection = new Matrix4f().perspective(Mathf.Radians(fov), aspect, near, far);
    }

    private void CalculateView() {
        view = Matrix4.View(gameObject.transform);
    }

    public Matrix4f GetProjection() {
        return projection;
    }

    public Matrix4f GetView() {
        return view;
    }

}
