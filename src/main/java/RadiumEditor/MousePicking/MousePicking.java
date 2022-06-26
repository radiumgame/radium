package RadiumEditor.MousePicking;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Graphics.Framebuffer.Framebuffer;
import Radium.Graphics.Mesh;
import Radium.Graphics.Vertex;
import Radium.Input.Input;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.Physics.PhysicsManager;
import Radium.Physics.PhysxUtil;
import Radium.PostProcessing.PostProcessing;
import Radium.SceneManagement.SceneManager;
import Radium.Variables;
import RadiumEditor.Console;
import RadiumEditor.Debug.Debug;
import RadiumEditor.ProjectExplorer;
import RadiumEditor.SceneHierarchy;
import RadiumEditor.Viewport;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30C;
import physx.common.PxVec3;
import physx.physics.PxActor;
import physx.physics.PxHitFlagEnum;
import physx.physics.PxRaycastBuffer10;
import physx.physics.PxRaycastHit;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.bgfx.BGFX.Functions.frame;

/**
 * Calculate mouse ray from 2D position
 */
public class MousePicking {

    public static Framebuffer framebuffer = new Framebuffer(1920, 1080);
    public static List<MeshRenderer> renderers = new ArrayList<MeshRenderer>();

    protected MousePicking() {}

    /**
     * Gets the mouse ray
     * @param viewportPosition {@link Viewport Viewport} editor window position
     * @param viewportSize {@link Viewport Viewport} editor window size
     * @return Mouse ray
     */
    public static Vector3 GetRay(Vector2 viewportPosition, Vector2 viewportSize) {
        Vector2 mouse = Input.GetMousePosition();
        float x = InverseLerp(viewportPosition.x, viewportPosition.x + viewportSize.x, 0, 1, mouse.x);
        float y = InverseLerp(viewportPosition.y, viewportPosition.y + viewportSize.y, 1, 0,mouse.y);

        if (x < 0) x = 0;
        if (x > 1) x = 1;
        if (y < 0) y = 0;
        if (y > 1) y = 1;

        x *= viewportSize.x;
        y *= viewportSize.y;
        mouse = new Vector2(x, y);

        Vector2 normalizedCoordinates = GetNormalizedDeviceCoordinates(viewportSize, mouse);
        Vector4f clipCoordinates = new Vector4f(normalizedCoordinates.x, normalizedCoordinates.y, -1f, 1f);
        Vector4f eyeCoordinates = ToEyeCoordinates(clipCoordinates);
        Vector3f worldRay = ToWorldCoordinates(eyeCoordinates);
        Vector3 radiumWorldRay = new Vector3(worldRay.x, worldRay.y, worldRay.z);

        return radiumWorldRay;
    }

    private static Vector2 GetNormalizedDeviceCoordinates(Vector2 viewportSize, Vector2 mousePosition) {
        float x = (2f * mousePosition.x) / viewportSize.x - 1f;
        float y = (2f * mousePosition.y) / viewportSize.y - 1f;

        return new Vector2(x, y);
    }

    private static Vector4f ToEyeCoordinates(Vector4f clipCoordinates) {
        Matrix4f invertedProjection = new Matrix4f();
        Variables.EditorCamera.GetProjection().invert(invertedProjection);
        Vector4f eyeCoordinates = invertedProjection.transform(clipCoordinates);

        return new Vector4f(eyeCoordinates.x, eyeCoordinates.y, -1f, 0f);
    }

    private static Vector3f ToWorldCoordinates(Vector4f eyeCoordinates) {
        Matrix4f invertedView = new Matrix4f();
        Variables.EditorCamera.GetView().invert(invertedView);
        Vector4f rayWorld = invertedView.transform(eyeCoordinates);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalize();

        return mouseRay;
    }

    public static void Render() {
        framebuffer.Bind();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(1, 1, 1, 1);
        GL11.glViewport(0, 0, 1920, 1080);

        for (MeshRenderer renderer : renderers) {
            renderer.MousePicking();
        }

        GL11.glFlush();
        GL11.glFinish();
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        Vector2 mouse = Input.GetMousePosition();
        Vector2 viewportPosition = Viewport.position, imageSize = Viewport.imageSize, imagePosition = Viewport.imagePosition;
        float x = InverseLerp(viewportPosition.x + imagePosition.x, viewportPosition.x + imagePosition.x + imageSize.x, 0, 1, mouse.x);
        float y = InverseLerp(viewportPosition.y + imagePosition.y, viewportPosition.y + imagePosition.y + imageSize.y, 1, 0,mouse.y);

        if (x < 0) x = 0;
        if (x > 1) x = 1;
        if (y < 0) y = 0;
        if (y > 1) y = 1;

        x *= 1920;
        y *= 1080;
        mouse = new Vector2(x, y);

        int[] data = new int[3];
        GL11.glReadPixels((int)mouse.x, (int)mouse.y,1,1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, data);
        int pickedID = data[0] + data[1] * 256 + data[2] * 256 * 256;
        if (ImGui.isMouseClicked(0) && !Viewport.UsingTransformationGizmo) {
            if (pickedID == 0 || pickedID - 1 >= SceneManager.GetCurrentScene().gameObjectsInScene.size()) {
                SceneHierarchy.current = null;
            } else {
                SceneHierarchy.current = SceneManager.GetCurrentScene().gameObjectsInScene.get(pickedID - 1);
                SceneHierarchy.OpenTreeNodes(SceneHierarchy.current);
                SceneHierarchy.ScrollTo(SceneHierarchy.current);
                ProjectExplorer.SelectedFile = null;
            }
        }

        framebuffer.Unbind();
        renderers.clear();
    }

    private static float InverseLerp(float imin, float imax, float omin, float omax, float v) {
        float t = (v - imin) / (imax - imin);
        float lerp = (1f - t) * omin + omax * t;

        return lerp;
    }

}
