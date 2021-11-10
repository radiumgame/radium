package Engine.Components.Demos;

import Editor.Console;
import Engine.Component;
import Engine.Components.Graphics.MeshFilter;
import Engine.Components.Graphics.MeshRenderer;
import Engine.Components.Physics.Rigidbody;
import Engine.Graphics.Mesh;
import Engine.Input.Input;
import Engine.Math.Vector.Vector3;
import Engine.ModelLoader;
import Engine.Objects.GameObject;
import Engine.PerformanceImpact;
import Engine.Physics.ForceMode;
import Engine.SceneManagement.SceneManager;
import Engine.Time;
import com.sun.jdi.connect.Connector;
import org.lwjgl.glfw.GLFW;

public class Shooter extends Component {

    public float timeBetweenShots = 0.3f;
    public float bulletForce = 2000f;
    private float time = 0;

    public Shooter() {
        description = "Shoots balls with a rigidbody attached";
        impact = PerformanceImpact.Low;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        time += Time.deltaTime;

        if (Input.GetKey(GLFW.GLFW_KEY_SPACE)) {
            if (time >= timeBetweenShots) {
                Shoot();
            }
        }
    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void UpdateVariable() {

    }

    @Override
    public void GUIRender() {

    }

    private void Shoot() {
        GameObject bullet = new GameObject();
        bullet.transform.position = gameObject.transform.position;
        bullet.transform.scale = new Vector3(0.2f, 0.2f, 0.2f);
        bullet.AddComponent(new MeshFilter(ModelLoader.LoadModel("EngineAssets/Sphere.fbx", "")[0]));
        bullet.AddComponent(new MeshRenderer());
        bullet.name = "Bullet";
        Rigidbody rb = (Rigidbody)bullet.AddComponent(new Rigidbody(new Vector3(0.2f, 0.2f, 0.2f)));
        rb.AddForce(Vector3.Multiply(gameObject.transform.Forward(), new Vector3(bulletForce, bulletForce, bulletForce)), ForceMode.Acceleration);

        time = 0;
    }

}
