package Editor.Debug;

import Engine.Graphics.Mesh;
import Engine.Graphics.Renderers.EditorRenderer;
import Engine.Math.Matrix4;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector3;
import Engine.ModelLoader;
import Engine.Objects.EditorObject;
import Engine.Util.NonInstantiatable;
import Engine.Variables;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public final class Debug extends NonInstantiatable {

    private static List<EditorObject> sceneObjects = new ArrayList<>();
    private static String blank = "EngineAssets/Textures/Misc/blank.jpg";

    public static void Cube(Vector3 position, float scale) {
        Mesh cube = Mesh.Cube(1, 1, blank);
        CreateEditorObject(position, scale, cube);
    }

    public static void Sphere(Vector3 position, float scale) {
        Mesh sphere = ModelLoader.LoadModel("EngineAssets/Sphere.fbx", blank)[0];
        CreateEditorObject(position, scale, sphere);
    }

    public static void Render() {
        Matrix4f view = Matrix4.View(Variables.EditorCamera.transform);

        for (EditorObject obj : sceneObjects) {
            Matrix4f model = Matrix4.Transform(obj.transform);
            EditorRenderer.Render(obj, model, view);
        }
    }

    private static void CreateEditorObject(Vector3 position, float scale, Mesh mesh) {
        Transform transform = new Transform();
        transform.position = position;
        transform.scale = new Vector3(scale, scale, scale);

        EditorObject newObject = new EditorObject(transform, mesh);

        sceneObjects.add(newObject);
    }

}
