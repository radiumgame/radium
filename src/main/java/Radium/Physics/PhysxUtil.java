package Radium.Physics;

import Radium.Math.Vector.Vector3;
import com.bulletphysics.linearmath.QuaternionUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import physx.common.PxQuat;
import physx.common.PxVec3;

import javax.vecmath.Quat4f;

/**
 * Methods to convert Nvida PhysX components to Radium components
 */
public class PhysxUtil {

    protected PhysxUtil() {}

    /**
     * Convert from {@link PxVec3 PxVec3} to {@link Vector3 Vector3}
     * @param vec Vector3 to convert
     */
    public static Vector3 FromPx3(PxVec3 vec) {
        return new Vector3(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Convert from {@link Vector3 Vector3} to {@link PxVec3 PxVec3}
     * @param vec Vector3 to convert
     */
    public static PxVec3 ToPx3(Vector3 vec) {
        return new PxVec3(vec.x, vec.y, vec.z);
    }

    /**
     * Set euler angles of a quaternion
     * @param euler Euler angle rotation
     * @return Quaternion with euler angles set
     */
    public static PxQuat SetEuler(Vector3 euler) {
        float x = euler.x;
        float y = euler.y;
        float z = euler.z;

        Quat4f quat = new Quat4f();
        QuaternionUtil.setEuler(quat, (float)Math.toRadians(y), (float)Math.toRadians(x), (float)Math.toRadians(z));
        PxQuat returnQuat = new PxQuat(quat.x, quat.y, quat.z, quat.w);

        return returnQuat;
    }

    /**
     * Convert quaternion to euler angles
     * @param quat Quaternion to convert
     * @return Euler angles
     */
    public static Vector3 GetEuler(PxQuat quat) {
        Vector3f eulerRadians = new Quaternionf(quat.getX(), quat.getY(), quat.getZ(), quat.getW()).getEulerAnglesXYZ(new Vector3f());
        Vector3 euler = new Vector3((float)Math.toDegrees(eulerRadians.x), (float)Math.toDegrees(eulerRadians.y), (float)Math.toDegrees(eulerRadians.z));

        return euler;
    }

}
