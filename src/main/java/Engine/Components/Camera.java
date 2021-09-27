package Engine.Components;

import Engine.Component;
import Engine.Variables;
import Engine.Window;
import org.joml.Matrix4f;

public class Camera extends Component {

    private Matrix4f projection;

    public float fov = 70f;
    public float near = 0.1f;
    public float far = 100f;

    @Override
    public void Start() {
        Variables.DefaultCamera = this;
    }

    @Override
    public void Update() {
        CalculateProjection();
    }

    public void CalculateProjection() {
        float aspect = (float)Window.width / (float)Window.height;
        projection = new Matrix4f().perspective((float)Math.toRadians(fov), aspect, near, far);
    }

    public Matrix4f GetProjection() {
        return projection;
    }

}
