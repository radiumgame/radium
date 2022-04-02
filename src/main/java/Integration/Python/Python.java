package Integration.Python;

import Integration.Project.Project;
import Radium.Color;
import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Components.Graphics.Outline;
import Radium.Graphics.Material;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.Scripting.Python.PythonScript;
import Radium.Time;
import Radium.Util.FileUtility;
import RadiumEditor.Console;
import org.python.core.PyBoolean;
import org.python.core.PyException;
import org.python.core.PyFloat;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.OutputStream;

public class Python {

    private PythonInterpreter interpreter;
    private transient PythonScript script;

    private PythonVariable deltaTime;

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
    }

    private void ApplyRadiumFunctions() {
        new PythonFunction("log", 1, (params) -> {
            Console.Log(params[0]);
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
            Vector3 col = new Vector3(params[0].asInt(), params[1].asInt(), params[2].asInt());
            if (script.gameObject.ContainsComponent(Outline.class)) {
                Outline outline = script.gameObject.GetComponent(Outline.class);
                outline.outlineColor = new Color((int)col.x, (int)col.y, (int)col.z);
            }
        }).Define(this);
    }

}
