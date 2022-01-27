package Radium.Math;

import Radium.Math.Vector.Vector3;
import org.joml.Vector3f;

public class Transform {

    public Vector3 position = Vector3.Zero;
    public Vector3 rotation = Vector3.Zero;
    public Vector3 scale = Vector3.One;

    public Vector3 Forward() {
        float x = Mathf.Sine(Mathf.Radians(rotation.y)) * Mathf.Cosine(Mathf.Radians(rotation.x));
        float y = Mathf.Sine(Mathf.Radians(-rotation.x));
        float z = Mathf.Cosine(Mathf.Radians(rotation.x)) * Mathf.Cosine(Mathf.Radians(rotation.y));

        return new Vector3(x, y, -z);
    }

    public Vector3 Back() {
        Vector3 forward = Forward();

        return new Vector3(-forward.x, -forward.y, -forward.z);
    }

    public Vector3 Right() {
        Vector3 forward = Forward();
        Vector3f forwardf = new Vector3f(forward.x, forward.y, forward.z);
        Vector3f cross = forwardf.cross(new Vector3f(0, 1, 0));
        Vector3 radiumCross = new Vector3(cross.x, cross.y, cross.z);

        return Vector3.Normalized(radiumCross);
    }

    public Vector3 Up() {
        Vector3 right = Right();
        Vector3 forward = Forward();

        Vector3f rightf = new Vector3f(right.x, right.y, right.z);
        Vector3f forwardf = new Vector3f(forward.x, forward.y, forward.z);

        Vector3f cross = rightf.cross(forwardf);
        Vector3 radiumCross = new Vector3(cross.x, cross.y, cross.z);

        return Vector3.Normalized(radiumCross);
    }

}
