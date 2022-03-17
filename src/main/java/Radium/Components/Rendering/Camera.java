package Radium.Components.Rendering;

import Radium.Component;
import Radium.Graphics.Framebuffer.DepthFramebuffer;
import RadiumEditor.Annotations.RunInEditMode;
import RadiumEditor.Debug.Gizmo.ComponentGizmo;
import Radium.Graphics.Texture;
import Radium.Math.Mathf;
import Radium.Math.Matrix4;
import Radium.PerformanceImpact;
import Radium.Variables;
import Radium.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

@RunInEditMode
public class Camera extends Component {

    /**
     * Field of view of camera
     */
    public float fov = 70f;
    /**
     * Near plane of camera
     */
    public float near = 0.1f;
    /**
     * Far plane of camera
     */
    public float far = 100f;

    private transient Matrix4f projection;
    private transient Matrix4f view;
    private transient ComponentGizmo gizmo;

    /**
     * Create empty camera component
     */
    public Camera() {
        icon = new Texture("EngineAssets/Editor/Icons/camera.png").textureID;
        Variables.DefaultCamera = this;

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

        if (Variables.DefaultCamera == this) {
            Variables.DefaultCamera = null;
        }
    }

    @Override
    public void UpdateVariable() {
        CalculateProjection();
        Variables.EditorCamera.CalculateProjection();
    }

    @Override
    public void GUIRender() {

    }

    /**
     * Recalculates the camera's projection matrices
     */
    public void CalculateProjection() {
        float aspect = (float)Window.width / (float)Window.height;
        projection = new Matrix4f().perspective(Mathf.Radians(fov), aspect, near, far);
    }

    private void CalculateView() {
        view = Matrix4.View(gameObject.transform, true);
    }

    /**
     * Returns the camera's current projection matrices
     * @return Camera projection matrices
     */
    public Matrix4f GetProjection() {
        return projection;
    }

    /**
     * Returns the camera's current view matrices
     * @return Camera view matrices
     */
    public Matrix4f GetView() {
        return view;
    }

}
