package Engine.Math;

import Engine.Math.Vector.Vector3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.vecmath.Quat4f;

public class QuaternionUtility {

    public static Vector3 GetEuler(Quat4f quat) {
        Vector3f eulerRadians = new Quaternionf(quat.x, quat.y, quat.z, quat.w).getEulerAnglesXYZ(new Vector3f());
        Vector3 euler = new Vector3(Mathf.Degrees(eulerRadians.x), Mathf.Degrees(eulerRadians.y), Mathf.Degrees(eulerRadians.z));

        return euler;
    }

    public static Vector3 GetEuler(Quaternionf quat) {
        Vector3f eulerRadians = new Quaternionf(quat.x, quat.y, quat.z, quat.w).getEulerAnglesXYZ(new Vector3f());
        Vector3 euler = new Vector3(Mathf.Degrees(eulerRadians.x), Mathf.Degrees(eulerRadians.y), Mathf.Degrees(eulerRadians.z));

        return euler;
    }

}
