package Editor;

import Editor.Debug.Debug;
import Engine.Graphics.Mesh;
import Engine.Graphics.Vertex;
import Engine.Input.Input;
import Engine.Math.QuaternionUtility;
import Engine.Math.Vector.Vector2;
import Engine.Math.Vector.Vector3;
import Engine.Objects.GameObject;
import Engine.Time;
import Engine.Util.NonInstantiatable;
import Engine.Variables;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.ShapeHull;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.convexhull.HullFlags;
import com.bulletphysics.util.ObjectArrayList;
import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;

public final class MousePicking extends NonInstantiatable {

    private static DynamicsWorld scene;
    private static float physicsTimeStep = 0.016f;
    private static float physicsTime = 0;

    private static int RayLength = 6000;

    private static HashMap<RigidBody, GameObject> colliders = new HashMap<>();

    public static void Initialize() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        scene = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        scene.setGravity(new javax.vecmath.Vector3f(0, 0, 0));
    }

    public static void Update() {
        physicsTime += Time.deltaTime;

        if (physicsTime >= 0) {
            physicsTime -= physicsTimeStep;

            scene.stepSimulation(physicsTimeStep);
        }
    }

    public static void AddObject(GameObject gameObject, Mesh mesh) {
        CollisionShape shape = CalculateHitbox(mesh);
        DefaultMotionState motionstate = new DefaultMotionState(new Transform(new javax.vecmath.Matrix4f(QuaternionUtility.SetEuler(gameObject.transform.rotation), new javax.vecmath.Vector3f(gameObject.transform.position.x, gameObject.transform.position.y, gameObject.transform.position.z), 1)));
        RigidBodyConstructionInfo rbinfo = new RigidBodyConstructionInfo(0f, motionstate, shape, new javax.vecmath.Vector3f(0,0,0));
        RigidBody body = new RigidBody(rbinfo);
        body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        scene.addRigidBody(body);
        colliders.put(body, gameObject);
    }

    private static CollisionShape CalculateHitbox(Mesh mesh) {
        ObjectArrayList<javax.vecmath.Vector3f> vertices = new ObjectArrayList<>();

        for (Vertex v : mesh.GetVertices()) {
            vertices.add(new javax.vecmath.Vector3f(v.GetPosition().x, v.GetPosition().y, v.GetPosition().z));
        }

        return new ConvexHullShape(vertices);
    }

    public static Vector3 GetRay(Vector2 viewportPosition, Vector2 viewportSize) {
        Vector2 mouse = Input.GetMousePosition();
        float x = InverseLerp(viewportPosition.x, viewportPosition.x + viewportSize.x, 0, 1, mouse.x);
        float y = InverseLerp(viewportPosition.y, viewportPosition.y + viewportSize.y, 1, 0,mouse.y);

        y += 0.05f;

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

    public static GameObject Raycast(Vector2 viewportPosition, Vector2 viewportSize) {
        if (ImGuizmo.isUsing()) return null;

        javax.vecmath.Vector3f position = Vecmath(Variables.EditorCamera.transform.position);

        Vector3 radiumRay = GetRay(viewportPosition, viewportSize);
        javax.vecmath.Vector3f ray = new javax.vecmath.Vector3f(-radiumRay.x, -radiumRay.y, -radiumRay.z);

        for (float i = 0; i < RayLength; i += 1) {
            CollisionWorld.ClosestRayResultCallback result = new CollisionWorld.ClosestRayResultCallback(position, ray);
            scene.rayTest(position, ray, result);

            if (result.hasHit()) {
                RigidBody body = (RigidBody)result.collisionObject;
                for (int j = 0; j < colliders.size(); j++) {
                    RigidBody obj = (RigidBody)colliders.keySet().toArray()[j];

                    if (body.debugBodyId == obj.debugBodyId) {
                        SceneHierarchy.current = colliders.get(obj);
                        break;
                    }
                }

                break;
            } else {
                position = Vecmath(GetPointOnRay(radiumRay, i));
            }
        }

        return SceneHierarchy.current;
    }

    private static Vector3 GetPointOnRay(Vector3 ray, float distance) {
        Vector3 cameraPosition = Variables.EditorCamera.transform.position;
        Vector3 scaledRay = new Vector3(ray.x * distance, ray.y * distance, ray.z * distance);

        return Vector3.Add(cameraPosition, scaledRay);
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

    private static javax.vecmath.Vector3f Lerp(javax.vecmath.Vector3f one, javax.vecmath.Vector3f two, float time) {
        Vector3f joml1 = new Vector3f(one.x, one.y, one.z);
        Vector3f joml2 = new Vector3f(two.x, two.y, two.z);
        Vector3f lerped = joml1.lerp(joml2, time);
        javax.vecmath.Vector3f javaxLerped = new javax.vecmath.Vector3f(lerped.x, lerped.y, lerped.z);

        return javaxLerped;
    }

    private static float InverseLerp(float imin, float imax, float omin, float omax, float v) {
        float t = (v - imin) / (imax - imin);
        float lerp = (1f - t) * omin + omax * t;

        return lerp;
    }

    private static javax.vecmath.Vector3f Vecmath(Vector3 vector) {
        return new javax.vecmath.Vector3f(vector.x, vector.y, vector.z);
    }

}
