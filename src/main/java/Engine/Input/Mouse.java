package Engine.Input;

import Engine.Math.Vector.Vector2;
import Engine.Math.Vector.Vector3;
import Engine.Util.NonInstantiatable;
import Engine.Variables;
import Engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class Mouse extends NonInstantiatable {

    public static Vector3 GetRay() {
        Vector2 mouse = Input.GetMousePosition();
        Vector2 normalizedCoordinates = GetNormalizedDeviceCoordinates(mouse);
        Vector4f clipCoordinates = new Vector4f(normalizedCoordinates.x, normalizedCoordinates.y, -1f, 1f);
        Vector4f eyeCoordinates = ToEyeCoordinates(clipCoordinates);
        Vector3f worldRay = ToWorldCoordinates(eyeCoordinates);
        Vector3 radiumWorldRay = new Vector3(worldRay.x, worldRay.y, worldRay.z);

        return radiumWorldRay;
    }

    private static Vector2 GetNormalizedDeviceCoordinates(Vector2 mousePosition) {
        float x = (2f * mousePosition.x) / Window.width - 1f;
        float y = (2f * mousePosition.y) / Window.height - 1f;

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

}
