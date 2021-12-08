package RadiumEditor.Debug;

import Radium.Graphics.BatchRendering.BatchRenderer;
import Radium.Graphics.BatchRendering.RenderBatch;
import Radium.Graphics.Mesh;
import Radium.Math.Mathf;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector3;
import Radium.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class GridLines {

    private static BatchRenderer renderer;
    private static RenderBatch batch;

    private static final int AmountOfLines = 50;
    private static final float LineWidth = 0.05f;
    private static final float LineLength = 50f;
    private static final float FarPlane = 70f;

    protected GridLines() {}

    public static void Initialize() {
        RenderBatch renderBatch = new RenderBatch(new ArrayList<>(), Mesh.Plane(LineWidth, LineLength, "EngineAssets/Textures/Misc/blank.jpg"));
        Matrix4f projection = new Matrix4f().perspective(Mathf.Radians(70f), (float)Window.width / (float)Window.height, 0.1f, FarPlane);
        renderer = new BatchRenderer(renderBatch, projection);
        batch = renderer.batch;

        CreateLines();
    }

    public static void Render() {
        GL11.glDepthMask(false);
        renderer.Render();
        GL11.glDepthMask(true);
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
