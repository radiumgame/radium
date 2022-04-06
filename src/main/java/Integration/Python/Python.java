package Integration.Python;

import Integration.API.API;
import Integration.Project.Project;
import Radium.*;
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
import Radium.Input.Input;
import Radium.Input.Keys;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.Physics.ColliderType;
import Radium.SceneManagement.Scene;
import Radium.SceneManagement.SceneManager;
import Radium.Scripting.Python.PythonScript;
import Radium.System.FileExplorer;
import Radium.Util.EnumUtility;
import Radium.Util.FileUtility;
import RadiumEditor.Console;
import RadiumEditor.Viewport;
import org.json.simple.JSONObject;
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
        new PythonFunction("getOpenScene", 0, (params) -> {
            Return("getOpenScene", new PyString(SceneManager.GetCurrentScene().file.getName()));
        }).Define(this);

        // API
        new PythonFunction("get", 1, (params) -> {
            String url = params[0].asString();
            JSONObject obj = API.Get(url);

            if (obj != null) {
                Return("get", new PyString(obj.toJSONString()));
            } else {
                Console.Error("Failed to get data at: " + url);
                Return("get", new PyString("404"));
            }
        }).Define(this);

        // Game Object
        new PythonFunction("getID", 0, (params) -> {
            String id = script.gameObject.id;
            Return("getID", new PyString(id));
        }).Define(this);
        new PythonFunction("create", 0, (params) -> {
            GameObject go = new GameObject();
            Return("create", new PyString(go.id));
        }).Define(this);
        new PythonFunction("createSphere", 0, (params) -> {
            GameObject go = new GameObject();
            go.AddComponent(new MeshFilter(ModelLoader.LoadModel("EngineAssets/Models/sphere.fbx", false).GetChildren().get(0).GetComponent(MeshFilter.class).mesh));
            go.AddComponent(new MeshRenderer());
            Return("createSphere", new PyString(go.id));
        }).Define(this);
        new PythonFunction("setName", 2, (params) -> {
            String id = params[0].asString();
            String name = params[1].asString();
            GameObject go = GameObject.Find(id);

            if (go != null) {
                go.name = name;
            } else {
                Console.Error("Cannot find GameObject with id: " + params[0].asString());
            }
        }).Define(this);
        new PythonFunction("setParent", 2, (params) -> {
            String objID = params[0].asString();
            String parentID = params[1].asString();
            GameObject child = GameObject.Find(objID);
            GameObject parent = GameObject.Find(parentID);

            if (parent != null && child != null) {
                child.SetParent(parent);
            } else {
                Console.Error("Cannot find GameObject with id: " + params[0].asString());
            }
        }).Define(this);
        new PythonFunction("removeParent", 1, (params) -> {
            GameObject obj = GameObject.Find(params[0].asString());

            if (obj != null) {
                obj.RemoveParent();
            } else {
                Console.Error("Cannot find GameObject with id: " + params[0].asString());
            }
        }).Define(this);
        new PythonFunction("getChild", 2, (params) -> {
            GameObject target = GameObject.Find(params[0].asString());
            int index = params[1].asInt();

            if (target == null) {
                Return("getChild", new PyString(""));
                Console.Error("Cannot find GameObject with id: " + params[0].asString());
                return;
            }

            List<GameObject> children = target.GetChildren();

            if (children.size() > index && index >= 0) {
                Return("getChild", new PyString(children.get(index).id));
            } else {
                Console.Error("Child at index " + index + " does not exist");
                Return("getChild", new PyString(""));
            }
        }).Define(this);
        new PythonFunction("destroy", 1, (params) -> {
            GameObject obj = GameObject.Find(params[0].asString());
            if (obj != null) {
                obj.Destroy();
            } else {
                Console.Error("Cannot find GameObject with id: " + params[0].asString());
            }
        }).Define(this);
        new PythonFunction("addComponentTo", 2, (params) -> {
            GameObject obj = GameObject.Find(params[0].asString());
            String compName = params[1].asString().toLowerCase();

            if (obj == null) {
                Console.Error("Cannot find GameObject with id: " + params[0].asString());
                return;
            }

            Component comp = TryGetComponent(compName);
            if (comp == null) return;
            try {
                obj.AddComponent(comp.getClass().getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                Console.Error(e);
            }
        }).Define(this);
        new PythonFunction("removeComponentFrom", 2, (params) -> {
            GameObject obj = GameObject.Find(params[0].asString());
            String compName = params[1].asString().toLowerCase();

            if (obj == null) {
                Console.Error("Cannot find GameObject with id: " + params[0].asString());
                return;
            }

            Component comp = TryGetComponent(compName);
            if (comp == null) return;
            if (obj.ContainsComponent(comp.getClass())) {
                obj.RemoveComponent(comp.getClass());
            }
        }).Define(this);
        new PythonFunction("setPositionOf", 4,(params) -> {
            GameObject obj = GameObject.Find(params[0].asString());
            if (obj == null) {
                Console.Error("Cannot find GameObject with id: " + params[0].asString());
                return;
            }

            Vector3 vec = new Vector3((float)params[1].asDouble(), (float)params[2].asDouble(), (float)params[3].asDouble());
            obj.transform.localPosition = vec;
        }).Define(this);
        new PythonFunction("setRotationOf", 4,(params) -> {
            GameObject obj = GameObject.Find(params[0].asString());
            if (obj == null) {
                Console.Error("Cannot find GameObject with id: " + params[0].asString());
                return;
            }

            Vector3 vec = new Vector3((float)params[1].asDouble(), (float)params[2].asDouble(), (float)params[3].asDouble());
            obj.transform.localRotation = vec;
        }).Define(this);
        new PythonFunction("setScaleOf", 4,(params) -> {
            GameObject obj = GameObject.Find(params[0].asString());
            if (obj == null) {
                Console.Error("Cannot find GameObject with id: " + params[0].asString());
                return;
            }

            Vector3 vec = new Vector3((float)params[1].asDouble(), (float)params[2].asDouble(), (float)params[3].asDouble());
            obj.transform.localScale = vec;
        }).Define(this);

        // Input
        boolean editor = Application.Editor;
        new PythonFunction("isKeyDown", 1, (params) -> {
            boolean useViewport = Viewport.ViewportHovered;

            String key = params[0].asString();
            if (key.length() == 1) {
                key = key.toUpperCase();
            }

            if (!List.of(EnumUtility.GetValues(Keys.class)).contains(key)) {
                Console.Error("No key of type " + key + " exists");
                Return("isKeyDown", new PyBoolean(false));
                return;
            }
            Keys keys = Keys.valueOf(key);
            Return("isKeyDown", new PyBoolean(!editor ? Input.GetKey(keys) : Input.GetKey(keys) && useViewport));
        }).Define(this);
        new PythonFunction("isMouseButtonDown", 1, (params) -> {
            boolean useViewport = Viewport.ViewportHovered;

            int button = params[0].asInt();
            Return("isMouseButtonDown", new PyBoolean(!editor ? Input.GetMouseButton(button) : Input.GetMouseButton(button) && useViewport));
        }).Define(this);
        new PythonFunction("isMouseButtonReleased", 1, (params) -> {
            boolean useViewport = Viewport.ViewportHovered;

            int button = params[0].asInt();
            Return("isMouseButtonReleased", new PyBoolean(!editor ? Input.GetMouseButtonReleased(button) : Input.GetMouseButtonReleased(button) && useViewport));
        }).Define(this);
        new PythonFunction("getMouseX", 0, (params) -> {
            Return("getMouseX", new PyFloat(Input.GetMouseX()));
        }).Define(this);
        new PythonFunction("getMouseY", 0, (params) -> {
            Return("getMouseY", new PyFloat(Input.GetMouseY()));
        }).Define(this);
        new PythonFunction("getScrollX", 0, (params) -> {
            Return("getScrollX", new PyFloat(Input.GetScrollX()));
        }).Define(this);
        new PythonFunction("getScrollY", 0, (params) -> {
            Return("getScrollY", new PyFloat(Input.GetScrollY()));
        }).Define(this);

        // System
        new PythonFunction("chooseFile", 1, (params) -> {
            String extensions = params[0].asString();
            String path = FileExplorer.Choose(extensions);
            Return("chooseFile", new PyString(path));
        });
        new PythonFunction("chooseFolder", 0, (params) -> {
            String path = FileExplorer.ChooseDirectory();
            Return("chooseFolder", new PyString(path));
        });

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
        new PythonFunction("getPosition", 0, (params) -> {
            PyArray vec = GetArray(script.gameObject.transform.localPosition);
            Return("getPosition", vec);
        }).Define(this);
        new PythonFunction("getRotation", 0, (params) -> {
            PyArray vec = GetArray(script.gameObject.transform.localRotation);
            Return("getRotation", vec);
        }).Define(this);
        new PythonFunction("getScale", 0, (params) -> {
            PyArray vec = GetArray(script.gameObject.transform.localScale);
            Return("getScale", vec);
        }).Define(this);
        new PythonFunction("getWorldPosition", 0, (params) -> {
            PyArray vec = GetArray(script.gameObject.transform.WorldPosition());
            Return("getWorldPosition", vec);
        }).Define(this);
        new PythonFunction("getWorldRotation", 0, (params) -> {
            PyArray vec = GetArray(script.gameObject.transform.WorldRotation());
            Return("getWorldRotation", vec);
        }).Define(this);
        new PythonFunction("getWorldScale", 0, (params) -> {
            PyArray vec = GetArray(script.gameObject.transform.WorldScale());
            Return("getWorldScale", vec);
        }).Define(this);
        new PythonFunction("forward", 0, (params) -> {
            Vector3 vec = script.gameObject.transform.Forward();
            PyArray pyVec = new PyArray(Float.class, new float[] { vec.x, vec.y, vec.z });
            Return("forward", pyVec);
        }).Define(this);
        new PythonFunction("backward", 0, (params) -> {
            Vector3 vec = Vector3.Multiply(script.gameObject.transform.Forward(), new Vector3(-1, -1, -1));
            PyArray pyVec = new PyArray(Float.class, new float[] { vec.x, vec.y, vec.z });
            Return("backward", pyVec);
        }).Define(this);
        new PythonFunction("left", 0, (params) -> {
            Vector3 vec = Vector3.Multiply(script.gameObject.transform.Right(), new Vector3(-1, -1, -1));
            PyArray pyVec = new PyArray(Float.class, new float[] { vec.x, vec.y, vec.z });
            Return("left", pyVec);
        }).Define(this);
        new PythonFunction("right", 0, (params) -> {
            Vector3 vec = script.gameObject.transform.Right();
            PyArray pyVec = new PyArray(Float.class, new float[] { vec.x, vec.y, vec.z });
            Return("right", pyVec);
        }).Define(this);
        new PythonFunction("up", 0, (params) -> {
            Vector3 vec = script.gameObject.transform.Up();
            PyArray pyVec = new PyArray(Float.class, new float[] { vec.x, vec.y, vec.z });
            Return("right", pyVec);
        }).Define(this);
        new PythonFunction("down", 0, (params) -> {
            Vector3 vec = Vector3.Multiply(script.gameObject.transform.Up(), new Vector3(-1, -1, -1));
            PyArray pyVec = new PyArray(Float.class, new float[] { vec.x, vec.y, vec.z });
            Return("right", pyVec);
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
        new PythonFunction("usingNormalMap", 0, (params) -> {
            MeshFilter mf = script.gameObject.GetComponent(MeshFilter.class);
            if (mf == null) {
                Console.Error("GameObject does not contain component of type Mesh Filter");
                Return("usingNormalMap", new PyBoolean(false));
                return;
            }

            Return("usingNormalMap", new PyBoolean(mf.material.useNormalMap));
        });
        new PythonFunction("useSpecularMap", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.useSpecularMap = use;
            }
        }).Define(this);
        new PythonFunction("usingSpecularMap", 0, (params) -> {
            MeshFilter mf = script.gameObject.GetComponent(MeshFilter.class);
            if (mf == null) {
                Console.Error("GameObject does not contain component of type Mesh Filter");
                Return("usingSpecularMap", new PyBoolean(false));
                return;
            }

            Return("usingSpecularMap", new PyBoolean(mf.material.useSpecularMap));
        });
        new PythonFunction("setReflectivity", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.reflectivity = val;
            }
        }).Define(this);
        new PythonFunction("getReflectivity", 0, (params) -> {
            MeshFilter mf = script.gameObject.GetComponent(MeshFilter.class);
            if (mf == null) {
                Console.Error("GameObject does not contain component of type Mesh Filter");
                Return("getReflectivity", new PyFloat(0));
                return;
            }

            Return("getReflectivity", new PyFloat(mf.material.reflectivity));
        });
        new PythonFunction("setShineDamper", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.shineDamper = val;
            }
        }).Define(this);
        new PythonFunction("getShineDamper", 0, (params) -> {
            MeshFilter mf = script.gameObject.GetComponent(MeshFilter.class);
            if (mf == null) {
                Console.Error("GameObject does not contain component of type Mesh Filter");
                Return("getShineDamper", new PyFloat(0));
                return;
            }

            Return("getShineDamper", new PyFloat(mf.material.shineDamper));
        });
        new PythonFunction("setColor", 3,(params) -> {
            Vector3 col = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            if (script.gameObject.ContainsComponent(MeshFilter.class)) {
                Material mat = script.gameObject.GetComponent(MeshFilter.class).material;
                mat.color = new Color(col.x / 255f, col.y / 255f, col.z / 255f);
            }
        }).Define(this);
        new PythonFunction("getColor", 0, (params) -> {
            MeshFilter mf = script.gameObject.GetComponent(MeshFilter.class);
            if (mf == null) {
                Console.Error("GameObject does not contain component of type Mesh Filter");
                Return("getColor", new PyFloat(0));
                return;
            }

            PyArray col = GetArray(Vector3.Multiply(mf.material.color.ToVector3(), new Vector3(255, 255, 255)));
            Return("getColor", col);
        });

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
        new PythonFunction("getOutlineWidth", 0,(params) -> {
            Outline outline = script.gameObject.GetComponent(Outline.class);
            if (outline == null) {
                Console.Error("GameObject does not contain component of type Outline");
                Return("getOutlineWidth", new PyFloat(0));
                return;
            }

            Return("getOutlineWidth", new PyFloat(outline.outlineWidth));
        }).Define(this);
        new PythonFunction("setOutlineColor", 3,(params) -> {
            Vector3 col = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            if (script.gameObject.ContainsComponent(Outline.class)) {
                Outline outline = script.gameObject.GetComponent(Outline.class);
                outline.outlineColor = new Color(col.x / 255f, col.y / 255f, col.z / 255f);
            }
        }).Define(this);
        new PythonFunction("getOutlineColor", 0,(params) -> {
            Outline outline = script.gameObject.GetComponent(Outline.class);
            if (outline == null) {
                Console.Error("GameObject does not contain component of type Outline");
                Return("getOutlineColor", new PyArray(Float.class, new float[] { 0, 0, 0 }));
                return;
            }

            Return("getOutlineColor", GetArray(Vector3.Multiply(outline.outlineColor.ToVector3(), new Vector3(255, 255, 255))));
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
        new PythonFunction("getParticleScale", 0,(params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps == null) {
                Console.Error("GameObject does not contain component of type Particle System");
                Return("getParticleScale", new PyArray(Float.class, new float[] { 0, 0 }));
                return;
            }

            Return("getParticleScale", new PyArray(Float.class, new float[] { ps.particleScale.x, ps.particleScale.y }));
        }).Define(this);
        new PythonFunction("setParticleColor", 3,(params) -> {
            Vector3 col = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.color = new Color(col.x / 255f, col.y / 255f, col.z / 255f);
            }
        }).Define(this);
        new PythonFunction("getParticleColor", 0,(params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps == null) {
                Console.Error("GameObject does not contain component of type Particle System");
                Return("getParticleColor", new PyArray(Float.class, new float[] { 0, 0, 0 }));
                return;
            }

            Return("getParticleColor", GetArray(Vector3.Multiply(ps.color.ToVector3(), new Vector3(255, 255, 255))));
        }).Define(this);
        new PythonFunction("useRandomParticleColor", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.randomColors = use;
            }
        }).Define(this);
        new PythonFunction("usingRandomParticleColor", 0, (params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps == null) {
                Console.Error("GameObject does not contain component of type Particle System");
                Return("usingRandomParticleColor", new PyBoolean(false));
                return;
            }

            Return("usingRandomParticleColor", new PyBoolean(ps.randomColors));
        }).Define(this);
        new PythonFunction("useParticleGravity", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.applyGravity = use;
            }
        }).Define(this);
        new PythonFunction("usingParticleGravity", 0, (params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps == null) {
                Console.Error("GameObject does not contain component of type Particle System");
                Return("usingParticleGravity", new PyBoolean(false));
                return;
            }

            Return("usingParticleGravity", new PyBoolean(ps.applyGravity));
        }).Define(this);
        new PythonFunction("useRandomParticleRotation", 1,(params) -> {
            boolean use = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.randomRotation = use;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("usingRandomParticleRotation", 0, (params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps == null) {
                Console.Error("GameObject does not contain component of type Particle System");
                Return("usingRandomParticleRotation", new PyBoolean(false));
                return;
            }

            Return("usingRandomParticleRotation", new PyBoolean(ps.randomRotation));
        }).Define(this);
        new PythonFunction("setParticleEmissionRate", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.emissionRate = val;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("getParticleEmissionRate", 0, (params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps == null) {
                Console.Error("GameObject does not contain component of type Particle System");
                Return("getParticleEmissionRate", new PyFloat(0));
                return;
            }

            Return("getParticleEmissionRate", new PyFloat(ps.emissionRate));
        }).Define(this);
        new PythonFunction("setParticleLifespan", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.particleLifespan = val;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("getParticleLifespan", 0, (params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps == null) {
                Console.Error("GameObject does not contain component of type Particle System");
                Return("getParticleLifespan", new PyFloat(0));
                return;
            }

            Return("getParticleLifespan", new PyFloat(ps.particleLifespan));
        }).Define(this);
        new PythonFunction("setParticleSpawnRange", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.particleSpawnRange = val;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("getParticleSpawnRange", 0, (params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps == null) {
                Console.Error("GameObject does not contain component of type Particle System");
                Return("getParticleSpawnRange", new PyFloat(0));
                return;
            }

            Return("getParticleSpawnRange", new PyFloat(ps.particleSpawnRange));
        }).Define(this);
        new PythonFunction("setParticleRotation", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.startRotation = val;
                ps.UpdateBatch();
            }
        }).Define(this);
        new PythonFunction("getParticleRotation", 0, (params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps == null) {
                Console.Error("GameObject does not contain component of type Particle System");
                Return("getParticleRotation", new PyFloat(0));
                return;
            }

            Return("getParticleRotation", new PyFloat(ps.startRotation));
        }).Define(this);
        new PythonFunction("setParticleTexture", 1, (params) -> {
            String val = Project.Current().assets + "/" + params[0].asString();
            if (script.gameObject.ContainsComponent(ParticleSystem.class)) {
                ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
                ps.material.path = val;
                ps.material.CreateMaterial();
            }
        }).Define(this);
        new PythonFunction("playParticles", 0, (params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps != null) {
                ps.PlayParticles();
            }
        }).Define(this);
        new PythonFunction("stopParticles", 0, (params) -> {
            ParticleSystem ps = script.gameObject.GetComponent(ParticleSystem.class);
            if (ps != null) {
                ps.StopParticles();
            }
        }).Define(this);

        // Rigidbody
        new PythonFunction("setMass", 1,(params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.mass = val;
                rb.UpdateBody();
            }
        }).Define(this);
        new PythonFunction("getMass", 0, (params) -> {
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb == null) {
                Console.Error("GameObject does not contain component of type Rigidbody");
                Return("getMass", new PyFloat(0));
                return;
            }

            Return("getMass", new PyFloat(rb.mass));
        }).Define(this);
        new PythonFunction("applyGravity", 1,(params) -> {
            boolean val = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.applyGravity = val;
            }
        }).Define(this);
        new PythonFunction("usingGravity", 0, (params) -> {
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb == null) {
                Console.Error("GameObject does not contain component of type Rigidbody");
                Return("usingGravity", new PyBoolean(false));
                return;
            }

            Return("usingGravity", new PyBoolean(rb.applyGravity));
        }).Define(this);
        new PythonFunction("setStatic", 1,(params) -> {
            boolean val = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.isStatic = val;
            }
        }).Define(this);
        new PythonFunction("isStatic", 0, (params) -> {
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb == null) {
                Console.Error("GameObject does not contain component of type Rigidbody");
                Return("isStatic", new PyBoolean(false));
                return;
            }

            Return("isStatic", new PyBoolean(rb.isStatic));
        }).Define(this);
        new PythonFunction("setKinematic", 1,(params) -> {
            boolean val = ((PyBoolean)params[0]).getBooleanValue();
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.isKinematic = val;
            }
        }).Define(this);
        new PythonFunction("isKinematic", 0, (params) -> {
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb == null) {
                Console.Error("GameObject does not contain component of type Rigidbody");
                Return("isKinematic", new PyBoolean(false));
                return;
            }

            Return("isKinematic", new PyBoolean(rb.isKinematic));
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
        new PythonFunction("getColliderRadius", 0, (params) -> {
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb == null) {
                Console.Error("GameObject does not contain component of type Rigidbody");
                Return("getColliderRadius", new PyFloat(0));
                return;
            }

            Return("getColliderRadius", new PyFloat(rb.GetColliderRadius()));
        }).Define(this);
        new PythonFunction("setColliderScale", 3, (params) -> {
            Vector3 scale = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            if (script.gameObject.ContainsComponent(Rigidbody.class)) {
                Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
                rb.SetScale(scale);
            }
        }).Define(this);
        new PythonFunction("getColliderScale", 0, (params) -> {
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb == null) {
                Console.Error("GameObject does not contain component of type Rigidbody");
                Return("getColliderScale", new PyArray(Float.class, new float[] { 0, 0, 0 }));
                return;
            }

            Return("getColliderScale", GetArray(rb.GetColliderScale()));
        }).Define(this);
        new PythonFunction("addForce", 3, (params) -> {
            Vector3 force = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb != null) {
                rb.AddForce(force);
            }
        }).Define(this);
        new PythonFunction("addTorque", 3, (params) -> {
            Vector3 torque = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb != null) {
                rb.AddTorque(torque);
            }
        }).Define(this);
        new PythonFunction("setVelocity", 3, (params) -> {
            Vector3 velocity = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb != null) {
                rb.SetVelocity(velocity);
            }
        }).Define(this);
        new PythonFunction("setAngularVelocity", 3, (params) -> {
            Vector3 velocity = new Vector3((float)params[0].asDouble(), (float)params[1].asDouble(), (float)params[2].asDouble());
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb != null) {
                rb.SetAngularVelocity(velocity);
            }
        }).Define(this);
        new PythonFunction("getVelocity", 0, (params) -> {
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb == null) {
                Console.Error("GameObject does not contain component of type Rigidbody");
                Return("getVelocity", new PyArray(Float.class, new float[] { 0, 0, 0 }));
                return;
            }

            Return("getVelocity", GetArray(rb.GetVelocity()));
        }).Define(this);
        new PythonFunction("getAngularVelocity", 0, (params) -> {
            Rigidbody rb = script.gameObject.GetComponent(Rigidbody.class);
            if (rb == null) {
                Console.Error("GameObject does not contain component of type Rigidbody");
                Return("getAngularVelocity", new PyArray(Float.class, new float[] { 0, 0, 0 }));
                return;
            }

            Return("getAngularVelocity", GetArray(rb.GetAngularVelocity()));
        }).Define(this);

        // Camera
        new PythonFunction("setCameraFOV", 1, (params) -> {
            float fov = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Camera.class)) {
                Camera cam = script.gameObject.GetComponent(Camera.class);
                cam.fov = fov;
                cam.CalculateProjection();
            }
        }).Define(this);
        new PythonFunction("setCameraNear", 1, (params) -> {
            float near = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Camera.class)) {
                Camera cam = script.gameObject.GetComponent(Camera.class);
                cam.near = near;
                cam.CalculateProjection();
            }
        }).Define(this);
        new PythonFunction("setCameraFar", 1, (params) -> {
            float far = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Camera.class)) {
                Camera cam = script.gameObject.GetComponent(Camera.class);
                cam.far = far;
                cam.CalculateProjection();
            }
        }).Define(this);
        new PythonFunction("getCameraFOV", 0, (params) -> {
            Camera cam = script.gameObject.GetComponent(Camera.class);
            if (cam != null) {
                Return("getCameraFOV", new PyFloat(cam.fov));
            } else {
                Console.Error("GameObject does not contain component of type Camera");
                Return("getCameraFOV", new PyFloat(0));
            }
        }).Define(this);
        new PythonFunction("getCameraNear", 0, (params) -> {
            Camera cam = script.gameObject.GetComponent(Camera.class);
            if (cam != null) {
                Return("getCameraNear", new PyFloat(cam.near));
            } else {
                Console.Error("GameObject does not contain component of type Camera");
                Return("getCameraNear", new PyFloat(0));
            }
        }).Define(this);
        new PythonFunction("getCameraFar", 0, (params) -> {
            Camera cam = script.gameObject.GetComponent(Camera.class);
            if (cam != null) {
                Return("getCameraFar", new PyFloat(cam.far));
            } else {
                Console.Error("GameObject does not contain component of type Camera");
                Return("getCameraFar", new PyFloat(0));
            }
        }).Define(this);

        // Light
        new PythonFunction("setLightColor", 3, (params) -> {
            Color col = new Color((float)params[0].asDouble() / 255f, (float)params[1].asDouble() / 255f, (float)params[2].asDouble() / 255f);
            if (script.gameObject.ContainsComponent(Light.class)) {
                Light light = script.gameObject.GetComponent(Light.class);
                light.color = col;
            }
        }).Define(this);
        new PythonFunction("getLightColor", 0, (params) -> {
            Light light = script.gameObject.GetComponent(Light.class);
            if (light == null) {
                Console.Error("GameObject does not contain component of type Light");
                Return("getLightColor", new PyArray(Float.class, new float[] { 0, 0, 0 }));
                return;
            }

            Return("getLightColor", GetArray(Vector3.Multiply(light.color.ToVector3(), new Vector3(255, 255, 255))));
        }).Define(this);
        new PythonFunction("setLightIntensity", 1, (params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Light.class)) {
                Light light = script.gameObject.GetComponent(Light.class);
                light.intensity = val;
            }
        }).Define(this);
        new PythonFunction("getLightIntensity", 0, (params) -> {
            Light light = script.gameObject.GetComponent(Light.class);
            if (light == null) {
                Console.Error("GameObject does not contain component of type Light");
                Return("getLightIntensity", new PyFloat(0));
                return;
            }

            Return("getLightIntensity", new PyFloat(light.intensity));
        }).Define(this);
        new PythonFunction("setLightAttenuation", 1, (params) -> {
            float val = (float)params[0].asDouble();
            if (script.gameObject.ContainsComponent(Light.class)) {
                Light light = script.gameObject.GetComponent(Light.class);
                light.attenuation = val;
            }
        }).Define(this);
        new PythonFunction("getLightAttenuation", 0, (params) -> {
            Light light = script.gameObject.GetComponent(Light.class);
            if (light == null) {
                Console.Error("GameObject does not contain component of type Light");
                Return("getLightAttenuation", new PyFloat(0));
                return;
            }

            Return("getLightAttenuation", new PyFloat(light.attenuation));
        }).Define(this);
        new PythonFunction("setLightType", 1, (params) -> {
            String type = params[0].asString();
            if (script.gameObject.ContainsComponent(Light.class)) {
                Light light = script.gameObject.GetComponent(Light.class);

                switch (type.toLowerCase()) {
                    case "directional" -> { light.lightType = LightType.Directional; }
                    case "point" -> { light.lightType = LightType.Point; }
                }
            }
        }).Define(this);

        // Image
        new PythonFunction("setImageTexture", 1, (params) -> {
            String path = Project.Current().assets + "/" + params[0].asString();
            if (script.gameObject.ContainsComponent(Image.class)) {
                Image img = script.gameObject.GetComponent(Image.class);
                img.mesh.texture = new Texture(path);
            }
        }).Define(this);
        new PythonFunction("setImagePosition", 2, (params) -> {
            Vector2 val = new Vector2((float)params[0].asDouble(), (float)params[1].asDouble());
            if (script.gameObject.ContainsComponent(Image.class)) {
                Image img = script.gameObject.GetComponent(Image.class);
                img.mesh.Position = val;
            }
        }).Define(this);
        new PythonFunction("getImagePosition", 0, (params) -> {
            Image image = script.gameObject.GetComponent(Image.class);
            if (image == null) {
                Console.Error("GameObject does not contain component of type Image");
                Return("getImagePosition", new PyArray(Float.class, new float[] { 0, 0 }));
                return;
            }

            Return("getImagePosition", new PyArray(Float.class, new float[] { image.mesh.Position.x, image.mesh.Position.y }));
        }).Define(this);
        new PythonFunction("setImageSize", 2, (params) -> {
            Vector2 val = new Vector2((float)params[0].asDouble(), (float)params[1].asDouble());
            if (script.gameObject.ContainsComponent(Image.class)) {
                Image img = script.gameObject.GetComponent(Image.class);
                img.mesh.Size = val;
            }
        }).Define(this);
        new PythonFunction("getImageSize", 0, (params) -> {
            Image image = script.gameObject.GetComponent(Image.class);
            if (image == null) {
                Console.Error("GameObject does not contain component of type Image");
                Return("getImageSize", new PyArray(Float.class, new float[] { 0, 0 }));
                return;
            }

            Return("getImageSize", new PyArray(Float.class, new float[] { image.mesh.Size.x, image.mesh.Size.y }));
        }).Define(this);
        new PythonFunction("setImageColor", 3, (params) -> {
            Color val = new Color((float)params[0].asDouble() / 255f, (float)params[1].asDouble() / 255f, (float)params[2].asDouble() / 255f);
            if (script.gameObject.ContainsComponent(Image.class)) {
                Image img = script.gameObject.GetComponent(Image.class);
                img.mesh.color = val;
            }
        }).Define(this);
        new PythonFunction("getImageColor", 0, (params) -> {
            Image image = script.gameObject.GetComponent(Image.class);
            if (image == null) {
                Console.Error("GameObject does not contain component of type Image");
                Return("getImageColor", new PyArray(Float.class, new float[] { 0, 0, 0 }));
                return;
            }

            Return("getImageColor", GetArray(Vector3.Multiply(image.mesh.color.ToVector3(), new Vector3(255, 255, 255))));
        }).Define(this);

        // Text
        new PythonFunction("setTextContent", 1, (params) -> {
            String content = params[0].asString();
            if (script.gameObject.ContainsComponent(Text.class)) {
                Text txt = script.gameObject.GetComponent(Text.class);
                txt.text = content;
                txt.CreateMeshes();
            }
        }).Define(this);
        new PythonFunction("getTextContent", 0, (params) -> {
            Text text = script.gameObject.GetComponent(Text.class);
            if (text == null) {
                Console.Error("GameObject does not contain component of type Text");
                Return("getTextContent", new PyString(""));
                return;
            }

            Return("getTextContent", new PyString(text.text));
        }).Define(this);
        new PythonFunction("setTextPosition", 2, (params) -> {
            Vector2 val = new Vector2((float)params[0].asDouble(), (float)params[1].asDouble());
            if (script.gameObject.ContainsComponent(Text.class)) {
                Text txt = script.gameObject.GetComponent(Text.class);
                txt.Position = val;
                txt.UpdateTransform();
            }
        }).Define(this);
        new PythonFunction("getTextPosition", 0, (params) -> {
            Text text = script.gameObject.GetComponent(Text.class);
            if (text == null) {
                Console.Error("GameObject does not contain component of type Text");
                Return("getTextPosition", new PyArray(Float.class, new float[] { 0, 0 }));
                return;
            }

            Return("getTextPosition", new PyArray(Float.class, new float[] { text.Position.x, text.Position.y }));
        }).Define(this);
        new PythonFunction("setTextColor", 3, (params) -> {
            Color val = new Color((float)params[0].asDouble() / 255f, (float)params[1].asDouble() / 255f, (float)params[2].asDouble() / 255f);
            if (script.gameObject.ContainsComponent(Text.class)) {
                Text txt = script.gameObject.GetComponent(Text.class);
                txt.color = val;
                txt.UpdateTransform();
            }
        }).Define(this);
        new PythonFunction("getTextColor", 0, (params) -> {
            Text text = script.gameObject.GetComponent(Text.class);
            if (text == null) {
                Console.Error("GameObject does not contain component of type Text");
                Return("getTextColor", new PyArray(Float.class, new float[] { 0, 0, 0 }));
                return;
            }

            Return("getTextColor", GetArray(Vector3.Multiply(text.color.ToVector3(), new Vector3(255, 255, 255))));
        }).Define(this);
        new PythonFunction("setTextFontSize", 1, (params) -> {
            int size = params[0].asInt();
            if (script.gameObject.ContainsComponent(Text.class)) {
                Text txt = script.gameObject.GetComponent(Text.class);
                txt.fontSize = size;
                txt.CreateMeshes();
            }
        }).Define(this);
        new PythonFunction("getTextFontSize", 0, (params) -> {
            Text text = script.gameObject.GetComponent(Text.class);
            if (text == null) {
                Console.Error("GameObject does not contain component of type Text");
                Return("getTextFontSize", new PyInteger(0));
                return;
            }

            Return("getTextFontSize", new PyInteger(text.fontSize));
        }).Define(this);
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

    private PyArray GetArray(Vector3 vector) {
        return new PyArray(Float.class, new float[] { vector.x, vector.y, vector.z });
    }

}
