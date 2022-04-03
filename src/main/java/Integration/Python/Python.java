package Integration.Python;

import Integration.Project.Project;
import Radium.Color;
import Radium.Component;
import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Components.Graphics.Outline;
import Radium.Components.Particles.ParticleSystem;
import Radium.Components.Physics.Rigidbody;
import Radium.Components.Rendering.Camera;
import Radium.Components.Rendering.Light;
import Radium.Components.UI.Image;
import Radium.Components.UI.Text;
import Radium.Graphics.Lighting.LightType;
import Radium.Graphics.Material;
import Radium.Graphics.Texture;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.Physics.ColliderType;
import Radium.SceneManagement.Scene;
import Radium.SceneManagement.SceneManager;
import Radium.Scripting.Python.PythonScript;
import Radium.Time;
import Radium.Util.FileUtility;
import RadiumEditor.Console;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Python {

    public transient HashMap<String, PythonFunction> functions = new HashMap<>();

    private PythonInterpreter interpreter;
    private transient PythonScript script;

    private PythonVariable deltaTime;
    private PythonVariable time;

    public Python(PythonScript script) {
        this.script = script;
    }

    public void Initialize() {
        if (interpreter != null) interpreter.close();
        interpreter = new PythonInterpreter();
        interpreter.setErr(OutputStream.nullOutputStream());

        ApplyRadiumVariables();
        ApplyRadiumFunctions();
    }

    public void Execute(String code) {
        interpreter.exec(code);
    }

    public void Execute(File f) {
        try {
            interpreter.execfile(f.getPath());
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void Update() {
        if (Time.deltaTime != 0) {
            deltaTime.SetValue(new PyFloat(Time.deltaTime));
        }
        time.SetValue(new PyFloat(Time.GetTime()));
    }

    public boolean TryCall(String function) {
        PyObject func = interpreter.get(function);
        if (func != null && func.isCallable()) {
            func.__call__();
            return true;
        }

        return false;
    }

    public PythonInterpreter GetInterpreter() {
        return interpreter;
    }

    private void ApplyRadiumVariables() {
        deltaTime = new PythonVariable("deltaTime", new PyFloat(Time.deltaTime));
        deltaTime.Define(this);

        time = new PythonVariable("time", new PyFloat(Time.GetTime()));
        time.Define(this);
    }

    private void ApplyRadiumFunctions() {
        new PythonFunction("log", 1, (params) -> {
            Console.Log(params[0]);
        }).Define(this);

        // Components
        new PythonFunction("addComponent", 1, (params) -> {
            String compName = params[0].asString().toLowerCase();

            Component comp = TryGetComponent(compName);
            if (comp == null) return;
            try {
                script.gameObject.AddComponent(comp.getClass().getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                Console.Error(e);
            }
        }).Define(this);
        new PythonFunction("removeComponent", 1, (params) -> {
            String compName = params[0].asString().toLowerCase();

            Component comp = TryGetComponent(compName);
            if (comp == null) return;
            if (script.gameObject.ContainsComponent(comp.getClass())) {
                script.gameObject.RemoveComponent(comp.getClass());
            }
        }).Define(this);
        new PythonFunction("hasComponent", 1, (params) -> {
            String name = params[0].asString();

            Component comp = TryGetComponent(name);
            if (comp == null) {
                Return("hasComponent", new PyBoolean(false));
                return;
            }
            Return("hasComponent", new PyBoolean(script.gameObject.ContainsComponent(comp.getClass())));
        }).Define(this);
        new PythonFunction("toggleComponent", 2, (params) -> {
            String compName = params[0].asString().toLowerCase();
            boolean enabled = ((PyBoolean)params[1]).getBooleanValue();

            Component comp = TryGetComponent(compName);
            if (comp == null) return;
            if (script.gameObject.ContainsComponent(comp.getClass())) {
                script.gameObject.GetComponent(comp.getClass()).enabled = enabled;
            }
        }).Define(this);
        new PythonFunction("isEnabled", 1, (params) -> {
            String name = params[0].asString();

            Component comp = TryGetComponent(name);
            if (comp == null) {
                Return("isEnabled", new PyBoolean(false));
                return;
            }
            if (script.gameObject.ContainsComponent(comp.getClass())) {
                Return("isEnabled", new PyBoolean(comp.enabled));
            } else {
                Console.Error("GameObject does not contain component of type " + name);
                Return("isEnabled", new PyBoolean(false));
            }
        }).Define(this);

        // Scene Management
        new PythonFunction("switchScene", 1, (params) -> {
            String path = Project.Current().assets + "/" + params[0].asString();
            SceneManager.SwitchScene(new Scene(path));
        }).Define(this);

        // Transform
        new PythonFunction("setPosition", 3,(params) -> {
            Vector3 vec = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            script.gameObject.transform.localPosition = vec;
        }).Define(this);
        new PythonFunction("setRotation", 3,(params) -> {
            Vector3 vec = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            script.gameObject.transform.localRotation = vec;
        }).Define(this);
        new PythonFunction("setScale", 3,(params) -> {
            Vector3 vec = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            script.gameObject.transform.localScale = vec;
        }).Define(this);
        new PythonFunction("translate", 3,(params) -> {
            Vector3 vec = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            script.gameObject.transform.localPosition = Vector3.Add(script.gameObject.transform.localPosition, vec);
        }).Define(this);
        new PythonFunction("rotate", 3,(params) -> {
            Vector3 vec = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            script.gameObject.transform.localRotation = Vector3.Add(script.gameObject.transform.localRotation, vec);
        }).Define(this);
        new PythonFunction("dilate", 3,(params) -> {
            Vector3 vec = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            script.gameObject.transform.localScale = Vector3.Add(script.gameObject.transform.localScale, vec);
        }).Define(this);

        // Mesh Filter
        new PythonFunction("setTexture", 1,(params) -> {
            String path = params[0].asString();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.path = Project.Current().assets + "/" + path;
                mat.CreateMaterial();
            }
        }).Define(this);
        new PythonFunction("setNormalMap", 1,(params) -> {
            String path = params[0].asString();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.specularMapPath = Project.Current().assets + "/" + path;
                mat.CreateMaterial();
            }
        }).Define(this);
        new PythonFunction("setSpecularMap", 1,(params) -> {
            String path = params[0].asString();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.specularMapPath = Project.Current().assets + "/" + path;
                mat.CreateMaterial();
            }
        }).Define(this);
        new PythonFunction("useSpecularLighting", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.specularLighting = use;
            }
        }).Define(this);
        new PythonFunction("useNormalMap", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.useNormalMap = use;
            }
        }).Define(this);
        new PythonFunction("useSpecularMap", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.useSpecularMap = use;
            }
        }).Define(this);
        new PythonFunction("setReflectivity", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.reflectivity = val;
            }
        }).Define(this);
        new PythonFunction("setShineDamper", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.shineDamper = val;
            }
        }).Define(this);
        new PythonFunction("setColor", 3,(params) -> {
            Vector3 col = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.color = new Color(col.x / 255f, col.y / 255f, col.z / 255f);
            }
        }).Define(this);

        // Mesh Renderer
        new PythonFunction("cullFaces", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(MeshRenderer.class)) {
                MeshRenderer mr = script.gameObject.GetComponent(MeshRenderer.class);
                mr.cullFaces = use;
            }
        }).Define(this);

        // Outline
        new PythonFunction("setOutlineWidth", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Outline.class)) {
                Outline outline = script.gameObject.GetComponent(Outline.class);
                outline.outlineWidth = val;
            }
        }).Define(this);
        new PythonFunction("setOutlineColor", 3,(params) -> {
            Vector3 col = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            if (script.gameObject.ContainsComponent(Outline.class)) {
                Outline outline = script.gameObject.GetComponent(Outline.class);
                outline.outlineColor = new Color(col.x / 255f, col.y / 255f, col.z / 255f);
            }
        }).Define(this);

        // Particle System
        new PythonFunction("setParticleScale", 2,(params) -> {
            Vector2 sca = new Vector2((float)params[0].asDouble(), (float)params[1].asDouble());
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.particleScale = sca;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("setParticleColor", 3,(params) -> {
            Vector3 col = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.color = new Color(col.x / 255f, col.y / 255f, col.z / 255f);
            }
        }).Define(this);
        new PythonFunction("useRandomParticleColor", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.randomColors = use;
            }
        }).Define(this);
        new PythonFunction("useParticleGravity", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.applyGravity = use;
            }
        }).Define(this);
        new PythonFunction("useParticleRandomRotation", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.randomRotation = use;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("setParticleEmissionRate", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.emissionRate = val;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("setParticleLifespan", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.particleLifespan = val;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("setParticleSpawnRange", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.particleSpawnRange = val;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("setParticleRotation", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.startRotation = val;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("setParticleTexture", 1, (params) -> {
            String val = Project.Current().assets + "/" + params[0].asString();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.material.path = val;
                ps.material.CreateMaterial();
            }
        }).Define(this);

        // Rigidbody
        new PythonFunction("setRigidbodyMass", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.mass = val;
                rb.UpdateBody();
            }
        }).Define(this);
        new PythonFunction("applyGravity", 1,(params) -> {
            boolean val = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.applyGravity = val;
            }
        }).Define(this);
        new PythonFunction("lockPosition", 1,(params) -> {
            boolean val = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.lockPosition = val;
            }
        }).Define(this);
        new PythonFunction("lockRotation", 1,(params) -> {
            boolean val = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.lockRotation = val;
            }
        }).Define(this);
        new PythonFunction("setColliderType", 1, (params) -> {
            String val = params[0].asString();
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                switch (val.toLowerCase()) {
                    case "box" -> { rb.collider = ColliderType.Box; }
                    case "sphere" -> { rb.collider = ColliderType.Sphere; }
                }
                rb.UpdateBody();
            }
        }).Define(this);
        new PythonFunction("setColliderRadius", 1, (params) -> {
            float radius = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.SetRadius(radius);
            }
        }).Define(this);
        new PythonFunction("setColliderScale", 3, (params) -> {
            Vector3 scale = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.SetScale(scale);
            }
        }).Define(this);

        // Camera
        new PythonFunction("setCameraFOV", 1, (params) -> {
            float fov = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Camera.class)) {
                Camera cam = script.gameObject.GetComponent(Camera.class);
                cam.fov = fov;
                cam.CalculateProjection();
            }
        }).Define(this);;
        new PythonFunction("setCameraNear", 1, (params) -> {
            float near = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Camera.class)) {
                Camera cam = script.gameObject.GetComponent(Camera.class);
                cam.near = near;
                cam.CalculateProjection();
            }
        }).Define(this);;
        new PythonFunction("setCameraFar", 1, (params) -> {
            float far = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Camera.class)) {
                Camera cam = script.gameObject.GetComponent(Camera.class);
                cam.far = far;
                cam.CalculateProjection();
            }
        }).Define(this);;

        // Light
        new PythonFunction("setLightColor", 3, (params) -> {
            Color col = new Color((float)params[0].asDouble() / 255f, (float)params[1].asDouble() / 255f, (float)params[2].asDouble() / 255f);
            if (script.gameObject.ContainsComponent(Light.class)) {
                Light light = script.gameObject.GetComponent(Light.class);
                light.color = col;
            }
        }).Define(this);;
        new PythonFunction("setLightIntensity", 1, (params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Light.class)) {
                Light light = script.gameObject.GetComponent(Light.class);
                light.intensity = val;
            }
        }).Define(this);;
        new PythonFunction("setLightAttenuation", 1, (params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Light.class)) {
                Light light = script.gameObject.GetComponent(Light.class);
                light.attenuation = val;
            }
        }).Define(this);;
        new PythonFunction("setLightType", 1, (params) -> {
            String type = params[0].asString();
            if (script.gameObject.ContainsComponent(Light.class)) {
                Light light = script.gameObject.GetComponent(Light.class);

                switch (type.toLowerCase()) {
                    case "directional" -> { light.lightType = LightType.Directional; }
                    case "point" -> { light.lightType = LightType.Point; }
                }
            }
        }).Define(this);;

        // Image
        new PythonFunction("setImageTexture", 1, (params) -> {
            String path = Project.Current().assets + "/" + params[0].asString();
            if (script.gameObject.ContainsComponent(Image.class)) {
                Image img = script.gameObject.GetComponent(Image.class);
                img.mesh.texture = new Texture(path);
            }
        }).Define(this);;
        new PythonFunction("setImagePosition", 2, (params) -> {
            Vector2 val = new Vector2((float)params[0].asDouble(), (float)params[1].asDouble());
            if (script.gameObject.ContainsComponent(Image.class)) {
                Image img = script.gameObject.GetComponent(Image.class);
                img.mesh.Position = val;
            }
        }).Define(this);;
        new PythonFunction("setImageSize", 2, (params) -> {
            Vector2 val = new Vector2((float)params[0].asDouble(), (float)params[1].asDouble());
            if (script.gameObject.ContainsComponent(Image.class)) {
                Image img = script.gameObject.GetComponent(Image.class);
                img.mesh.Size = val;
            }
        }).Define(this);;
        new PythonFunction("setImageColor", 3, (params) -> {
            Color val = new Color((float)params[0].asDouble() / 255f, (float)params[1].asDouble() / 255f, (float)params[2].asDouble() / 255f);
            if (script.gameObject.ContainsComponent(Image.class)) {
                Image img = script.gameObject.GetComponent(Image.class);
                img.mesh.color = val;
            }
        }).Define(this);;

        // Text
        new PythonFunction("setTextContent", 1, (params) -> {
            String content = params[0].asString();
            if (script.gameObject.ContainsComponent(Text.class)) {
                Text txt = script.gameObject.GetComponent(Text.class);
                txt.text = content;
                txt.CreateMeshes();
            }
        }).Define(this);;
        new PythonFunction("setTextPosition", 2, (params) -> {
            Vector2 val = new Vector2((float)params[0].asDouble(), (float)params[1].asDouble());
            if (script.gameObject.ContainsComponent(Text.class)) {
                Text txt = script.gameObject.GetComponent(Text.class);
                txt.Position = val;
                txt.UpdateTransform();
            }
        }).Define(this);;
        new PythonFunction("setTextColor", 3, (params) -> {
            Color val = new Color((float)params[0].asDouble() / 255f, (float)params[1].asDouble() / 255f, (float)params[2].asDouble() / 255f);
            if (script.gameObject.ContainsComponent(Text.class)) {
                Text txt = script.gameObject.GetComponent(Text.class);
                txt.color = val;
                txt.UpdateTransform();
            }
        }).Define(this);;
        new PythonFunction("setFontSize", 1, (params) -> {
            int size = params[0].asInt();
            if (script.gameObject.ContainsComponent(Text.class)) {
                Text txt = script.gameObject.GetComponent(Text.class);
                txt.fontSize = size;
                txt.CreateMeshes();
            }
        }).Define(this);;
    }

    private void Return(String name, PyObject returnValue) {
        functions.get(name).returnObject = returnValue;
    }

    private Component TryGetComponent(String compName) {
        compName = compName.toLowerCase();
        List<String> compNames = List.of(Component.ComponentNames());
        if (compNames.contains(compName)) {
            for (Component comp : Component.ComponentTypes()) {
                if (comp.name.toLowerCase().equals(compName)) {
                    return comp;
                }
            }
        } else {
            Console.Error("Component with the name " + compName + " doesn't exist");
        }

        return null;
    }

}
