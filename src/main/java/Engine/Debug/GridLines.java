package Engine.Debug;

import Engine.Graphics.BatchRendering.BatchRenderer;
import Engine.Graphics.BatchRendering.RenderBatch;
import Engine.Graphics.Mesh;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector3;
import Engine.Util.NonInstantiatable;
import Engine.Window;
import org.joml.Matrix4f;

import java.util.ArrayList;

public final class GridLines extends NonInstantiatable {

    private static BatchRenderer renderer;
    private static RenderBatch batch;

    private static final int AmountOfLines = 50;
    private static final float LineWidth = 0.05f;
    private static final float LineLength = 50f;
    private static final float FarPlane = 70f;

    public static void Initialize() {
        RenderBatch renderBatch = new RenderBatch(new ArrayList<>(), Mesh.Plane(LineWidth, LineLength, "EngineAssets/Textures/blank.jpg"));
        Matrix4f projection = new Matrix4f().perspective((float)Math.toRadians(70.0f), (float)Window.width / (float)Window.height, 0.1f, FarPlane);
        renderer = new BatchRenderer(renderBatch, projection);
        batch = renderer.batch;

        CreateLines();
    }

    public static void Render() {
        renderer.Render();
    }

    private static void CreateLines() {
        for (int x = 0; x < AmountOfLines; x++) {
            for (int z = 0; z < AmountOfLines; z++) {
                batch.AddObject(CalculateTransform(x, z));
            }
        }
    }

    private static Transform CalculateTransform(int x, int z) {
        Transform transform = new Transform();
        transform.position = new Vector3(x - AmountOfLines / 2, 0, z);

        if (z != 0) {
            transform.position.z -= AmountOfLines / 2;
            transform.rotation = new Vector3(0, -90, 0);
            transform.scale = new Vector3(1, 1, 0.1f);
        } else {
            transform.scale = Vector3.One;
        }

        return transform;
    }

}
