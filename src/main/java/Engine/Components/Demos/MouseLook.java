package Engine.Components.Demos;

import Engine.Component;
import Engine.Input.Input;
import Engine.Math.Axis;
import Engine.Math.Vector.Vector3;
import Engine.Time;

public class MouseLook extends Component {

    public float mouseSensitivity = 0.2f;
    public float cameraClamp = 80;

    private double oldMouseX = 0, oldMouseY = 0, newMouseX = 0, newMouseY = 0;

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        newMouseX = Input.GetMouseX();
        newMouseY = Input.GetMouseY();

        float dx = (float) (newMouseX - oldMouseX);
        float dy = (float) (newMouseY - oldMouseY);
        gameObject.transform.rotation = Vector3.Add(gameObject.transform.rotation, new Vector3(dy * mouseSensitivity * Time.deltaTime, dx * mouseSensitivity * Time.deltaTime, 0));

        gameObject.transform.rotation.Clamp(Axis.X, -cameraClamp, cameraClamp);

        oldMouseX = newMouseX;
        oldMouseY = newMouseY;
    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void OnVariableUpdate() {

    }

    @Override
    public void GUIRender() {

    }

}
