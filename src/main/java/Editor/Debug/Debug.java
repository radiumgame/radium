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
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public final class Debug extends NonInstantiatable {

    private static HashMap<Integer, EditorObject> sceneObjects = new HashMap<>();
    private static String blank = "EngineAssets/Textures/Misc/blank.jpg";
    private static float lineWidth = 0.05f;

    private static int currentID = 0;

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

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glLineWidth(lineWidth);
        for (EditorObject obj : sceneObjects.values()) {
            Matrix4f model = Matrix4.Transform(obj.transform);
            EditorRenderer.Render(obj, model, view);
        }
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
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
