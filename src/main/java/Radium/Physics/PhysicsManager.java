package Radium.Physics;

import Radium.Time;
import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.extensions.PxDefaultAllocator;
import physx.physics.*;

public class PhysicsManager {

    private static PxDefaultAllocator allocator;
    private static PxDefaultErrorCallback errorCallback;
    private static PxFoundation foundation;
    private static PxTolerancesScale tolerances;
    private static PxPhysics physics;
    private static PxScene scene;

    private static int ThreadCount = 4;

    private static float physicsTimeStep = 0.02f;
    private static float physicsTime = 0;
    private static boolean initialized = false;

    public static int PhysxVersion;

    protected PhysicsManager() {}

    public static void Initialize() {
        PhysxVersion = PxTopLevelFunctions.getPHYSICS_VERSION();

        allocator = new PxDefaultAllocator();
        errorCallback = new PxDefaultErrorCallback();
        foundation = PxTopLevelFunctions.CreateFoundation(PhysxVersion, allocator, errorCallback);

        tolerances = new PxTolerancesScale();
        physics = PxTopLevelFunctions.CreatePhysics(PhysxVersion, foundation, tolerances);

        PxSceneDesc sceneDesc = new PxSceneDesc(tolerances);
        sceneDesc.setGravity(new PxVec3(0f, -9.81f, 0f));
        sceneDesc.setCpuDispatcher(PxTopLevelFunctions.DefaultCpuDispatcherCreate(ThreadCount));
        sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
        sceneDesc.getFlags().clear(PxSceneFlagEnum.eENABLE_PCM);
        scene = physics.createScene(sceneDesc);
    }

    public static void Update() {
        physicsTime += Time.deltaTime;

        if (physicsTime >= 0) {
            physicsTime -= physicsTimeStep;

            if (initialized) scene.fetchResults();
            scene.simulate(physicsTimeStep);

            initialized = true;
        }
    }

    public static PxPhysics GetPhysics() {
        return physics;
    }

    public static PxScene GetPhysicsScene() {
        return scene;
    }

}
