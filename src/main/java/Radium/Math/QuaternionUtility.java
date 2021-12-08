package Radium.Math;

import Radium.Math.Vector.Vector3;
import com.bulletphysics.linearmath.QuaternionUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.vecmath.Quat4f;

public class QuaternionUtility {

    public static Quat4f SetEuler(Vector3 value) {
        Quat4f quat = new Quat4f();
        QuaternionUtil.setEuler(quat, value.x, value.y, value.z);

        return quat;
    }

    public static Quaternionf SetEulerJOML(Vector3 value) {
        Quat4f quat = SetEuler(value);
        Quaternionf jomlQuat = new Quaternionf(quat.x, quat.y, quat.z, quat.w);

        return jomlQuat;
    }

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

    public static Vector3 LookAt(Transform object, Vector3 point) {
        if (object.position == point) {
            return Vector3.Zero;
        }

        Vector3 difference = Vector3.Subtract(point, object.position);
        Quaternionf quaternionRotation = new Quaternionf().lookAlong(new Vector3f(difference.x, difference.y, difference.z), new Vector3f(0, 1, 0));
        Vector3 rotation = QuaternionUtility.GetEuler(quaternionRotation);

        return rotation;
    }

}