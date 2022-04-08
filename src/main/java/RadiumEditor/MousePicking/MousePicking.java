package RadiumEditor.MousePicking;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Graphics.Mesh;
import Radium.Graphics.Vertex;
import Radium.Input.Input;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Objects.GameObject;
import Radium.Physics.PhysicsManager;
import Radium.Physics.PhysxUtil;
import Radium.SceneManagement.SceneManager;
import Radium.Variables;
import RadiumEditor.Console;
import RadiumEditor.Debug.Debug;
import RadiumEditor.Viewport;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import physx.common.PxVec3;
import physx.physics.PxActor;
import physx.physics.PxHitFlagEnum;
import physx.physics.PxRaycastBuffer10;
import physx.physics.PxRaycastHit;

/**
 * Calculate mouse ray from 2D position
 */
public class MousePicking {

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

    private static float InverseLerp(float imin, float imax, float omin, float omax, float v) {
        float t = (v - imin) / (imax - imin);
        float lerp = (1f - t) * omin + omax * t;

        return lerp;
    }

    public static GameObject DetectCollision(Vector3 ray) {
        /*
        GameObject closestObject = null;
        float closestDistance = maxDistance;
        int depth = 2;

        for (int i = 1; i < recursion + 1; i += depth) {
            Vector3 newPosition = Vector3.Add(Variables.EditorCamera.transform.position, Vector3.Multiply(ray, new Vector3(i, i, i)));

            for (GameObject go : SceneManager.GetCurrentScene().gameObjectsInScene) {
                if (!go.ContainsComponent(MeshRenderer.class) || !go.ContainsComponent(MeshFilter.class)) continue;

                MeshFilter filter = go.GetComponent(MeshFilter.class);
                Mesh mesh = filter.mesh;
                for (Vertex vertex : mesh.GetVertices()) {
                    float distance = Vector3.Distance(Vector3.Add(go.transform.WorldPosition(), Vector3.Multiply(vertex.GetPosition(), go.transform.WorldScale())), newPosition);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestObject = go;
                    }
                }
            }
        }

        return closestObject;
         */

        PxRaycastBuffer10 hitBuffer = new PxRaycastBuffer10();
        PxVec3 rayOrigin = PhysxUtil.ToPx3(Variables.EditorCamera.transform.position);
        PxVec3 rayDir = PhysxUtil.ToPx3(ray);
        if (MousePickingCollision.GetScene().raycast(rayOrigin, rayDir, 100, hitBuffer)) {
            PxActor actor = hitBuffer.getAnyHit(0).getActor();
            GameObject obj = GameObject.Find(actor.getName());
            return obj;
        }

        return null;
    }

}
