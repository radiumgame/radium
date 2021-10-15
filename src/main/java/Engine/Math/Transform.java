package Engine.Math;

import Engine.Component;
import Engine.Math.Vector.Vector3;
import org.joml.Vector3f;

public class Transform {

    public Vector3 position = Vector3.Zero;
    public Vector3 rotation = Vector3.Zero;
    public Vector3 scale = Vector3.One;

    public Vector3 Forward() {
        float x = (float)Math.sin(Math.toRadians(rotation.y)) * (float)Math.cos(Math.toRadians(rotation.x));
        float y = (float)Math.sin(Math.toRadians(-rotation.x));
        float z = (float)Math.cos(Math.toRadians(rotation.x)) * (float)Math.cos(Math.toRadians(rotation.y));

        return new Vector3(x, y, -z);
    }

    public Vector3 Back() {
        Vector3 forward = Forward();

        return new Vector3(-forward.x, -forward.y, -forward.z);
    }

}
