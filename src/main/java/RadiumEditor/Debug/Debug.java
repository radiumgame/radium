package RadiumEditor.Debug;

import Radium.Graphics.Mesh;
import Radium.Graphics.Renderers.EditorRenderer;
import Radium.Math.Matrix4;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector3;
import Radium.ModelLoader;
import Radium.Objects.EditorObject;
import Radium.Variables;
import org.joml.Matrix4f;

import java.util.HashMap;

public class Debug {

    private static HashMap<Integer, EditorObject> sceneObjects = new HashMap<>();
    private static String blank = "EngineAssets/Textures/Misc/blank.jpg";

    private static int currentID = 0;

    protected Debug() {}

    public static int CreateCube(Vector3 position, float scale) {
        Mesh cube = Mesh.Cube(1, 1, blank);
        return CreateEditorObject(position, scale, cube);
    }

    public static int CreateSphere(Vector3 position, float scale) {
        Mesh sphere = ModelLoader.LoadModel("EngineAssets/Sphere.fbx", blank)[0];
        return CreateEditorObject(position, scale, sphere);
    }

    public static void DestroyEntity(int id) {
        sceneObjects.remove(id);
    }

    public static void Render() {
        Matrix4f view = Matrix4.View(Variables.EditorCamera.transform);

        for (EditorObject obj : sceneObjects.values()) {
            Matrix4f model = Matrix4.Transform(obj.transform);
            EditorRenderer.Render(obj, model, view);
        }
    }

    private static int CreateEditorObject(Vector3 position, float scale, Mesh mesh) {
        Transform transform = new Transform();
        transform.position = position;
        transform.scale = new Vector3(scale, scale, scale);

        EditorObject newObject = new EditorObject(transform, mesh);

        int id = currentID;
        sceneObjects.put(id, newObject);

        currentID++;
        return id;
    }

}
