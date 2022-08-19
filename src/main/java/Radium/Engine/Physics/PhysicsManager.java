package Radium.Engine.Physics;

import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.Time;
import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.cooking.PxCooking;
import physx.cooking.PxCookingParams;
import physx.extensions.PxDefaultAllocator;
import physx.physics.*;

/**
 * Contains physics components such as the world and all rigidbodies
 */
public class PhysicsManager {

    private static PxDefaultAllocator allocator;
    private static PxDefaultErrorCallback errorCallback;
    private static PxFoundation foundation;
    private static PxTolerancesScale tolerances;
    private static PxPhysics physics;
    private static PxScene scene;
    private static PxCooking cooking;

    private static int ThreadCount = 4;

    private static final float physicsTimeStep = 0.02f;
    private static float physicsTime = 0;
    private static boolean Created = false;

    public static int PhysxVersion;

    protected PhysicsManager() {}

    /**
     * Initialize the physics world
     */
    public static void Initialize() {
        if (Created) return;
        PhysxVersion = PxTopLevelFunctions.getPHYSICS_VERSION();

        allocator = new PxDefaultAllocator();
        errorCallback = new PxDefaultErrorCallback();

        tolerances = new PxTolerancesScale();

        foundation = PxTopLevelFunctions.CreateFoundation(PhysxVersion, allocator, errorCallback);
        physics = PxTopLevelFunctions.CreatePhysics(PhysxVersion, foundation, tolerances);

        cooking = PxTopLevelFunctions.CreateCooking(PhysxVersion, foundation, new PxCookingParams(tolerances));

        PxSceneDesc sceneDesc = new PxSceneDesc(tolerances);
        sceneDesc.setGravity(new PxVec3(0f, -9.81f, 0f));
        sceneDesc.setCpuDispatcher(PxTopLevelFunctions.DefaultCpuDispatcherCreate(ThreadCount));
        sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
        sceneDesc.getFlags().clear(PxSceneFlagEnum.eENABLE_PCM);
        scene = physics.createScene(sceneDesc);

        Created = true;
    }

    /**
     * Update the physics world by the physics timestep
     */
    public static void Update() {
        physicsTime += Time.deltaTime;

        if (physicsTime >= 0) {
            physicsTime -= physicsTimeStep;
            scene.simulate(physicsTimeStep);
            scene.fetchResults(true);
        }
    }

    public static RaycastHit Raycast(Vector3 origin, Vector3 direction, float distance) {
        PxRaycastBuffer10 hitBuffer = new PxRaycastBuffer10();
        PxVec3 rayOrigin = PhysxUtil.ToPx3(origin);
        PxVec3 rayDir = PhysxUtil.ToPx3(direction);
        if (scene.raycast(rayOrigin, rayDir, distance, hitBuffer)) {
            PxActor actor = hitBuffer.getAnyHit(0).getActor();
            GameObject obj = GameObject.Find(actor.getName());

            RaycastHit hit = new RaycastHit(obj, PhysxUtil.FromPx3(hitBuffer.getAnyHit(0).getPosition()));
            return hit;
        }

        return null;
    }

    public static PxCooking GetCooking() {
        return cooking;
    }

    /**
     * Returns the physics world that the engine uses
     * @return Physics world
     */
    public static PxPhysics GetPhysics() {
        return physics;
    }

    /**
     * Returns the current physics scene being used
     * @return Physics scene
     */
    public static PxScene GetPhysicsScene() {
        return scene;
    }

}
