package Radium.Editor.Debug;

import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Graphics.Material;
import Radium.Engine.Graphics.Mesh;
import Radium.Engine.Graphics.Renderers.EditorRenderer;
import Radium.Engine.Math.Matrix4;
import Radium.Engine.Math.Transform;
import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.ModelLoader;
import Radium.Engine.Objects.EditorObject;
import Radium.Engine.Variables;
import org.joml.Matrix4f;

import java.util.HashMap;

/**
 * Can draw models in edit mode
 */
public class Debug {

    private static HashMap<Integer, EditorObject> sceneObjects = new HashMap<>();
    private static String blank = "EngineAssets/Textures/Misc/blank.jpg";

    private static int currentID = 0;

    protected Debug() {}

    /**
     * Draws cube in editor
     * @param position Mesh position
     * @param scale Mesh scale
     * @return Editor object ID
     */
    public static int CreateCube(Vector3 position, float scale) {
        Mesh cube = Mesh.Cube(1, 1);
        return CreateEditorObject(position, scale, cube);
    }

    /**
     * Draws sphere in editor
     * @param position Mesh position
     * @param scale Mesh scale
     * @return Editor object ID
     */
    public static int CreateSphere(Vector3 position, float scale) {
        Mesh sphere = ModelLoader.LoadModel("EngineAssets/Models/Sphere.fbx", false).GetChildren().get(0).GetChildren().get(0).GetComponent(MeshFilter.class).mesh;
        return CreateEditorObject(position, scale, sphere);
    }

    /**
     * Destroys editor object
     * @param id Editor object ID
     */
    public static void DestroyEntity(int id) {
        sceneObjects.remove(id);
    }

    /**
     * Renders all editor objects
     */
    public static void Render() {
        Matrix4f view = Matrix4.View(Variables.EditorCamera.transform);

        for (EditorObject obj : sceneObjects.values()) {
            Matrix4f model = Matrix4.Transform(obj.transform, false);
            EditorRenderer.Render(obj, model, view);
        }
    }

    private static int CreateEditorObject(Vector3 position, float scale, Mesh mesh) {
        Transform transform = new Transform();
        transform.position = position;
        transform.scale = new Vector3(scale, scale, scale);

        EditorObject newObject = new EditorObject(transform, mesh, new Material(blank));

        int id = currentID;
        sceneObjects.put(id, newObject);

        currentID++;
        return id;
    }

}
