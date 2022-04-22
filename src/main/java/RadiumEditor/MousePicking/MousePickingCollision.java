package RadiumEditor.MousePicking;

import Radium.Time;
import physx.PxTopLevelFunctions;
import physx.common.PxDefaultErrorCallback;
import physx.common.PxFoundation;
import physx.common.PxTolerancesScale;
import physx.common.PxVec3;
import physx.cooking.PxCooking;
import physx.cooking.PxCookingParams;
import physx.extensions.PxDefaultAllocator;
import physx.physics.PxPhysics;
import physx.physics.PxScene;
import physx.physics.PxSceneDesc;
import physx.physics.PxSceneFlagEnum;

public class MousePickingCollision {

    private static PxDefaultAllocator allocator;
    private static PxDefaultErrorCallback errorCallback;
    public static PxFoundation foundation;
    private static PxTolerancesScale tolerances;
    private static PxPhysics physics;
    private static PxScene scene;
    private static PxCooking cooking;

    private static int ThreadCount = 4;

    private static float physicsTimeStep = 0.02f;
    private static float physicsTime = 0;
    private static boolean initialized = false;

    public static void Initialize() {
        int PhysxVersion = PxTopLevelFunctions.getPHYSICS_VERSION();

        allocator = new PxDefaultAllocator();
        errorCallback = new PxDefaultErrorCallback();
        foundation = PxTopLevelFunctions.CreateFoundation(PhysxVersion, allocator, errorCallback);

        tolerances = new PxTolerancesScale();
        physics = PxTopLevelFunctions.CreatePhysics(PhysxVersion, foundation, tolerances);

        PxCookingParams params = new PxCookingParams(tolerances);
        cooking = PxTopLevelFunctions.CreateCooking(PhysxVersion, foundation, params);

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

    public static PxScene GetScene() {
        return scene;
    }

    public static PxCooking GetCooking() {
        return cooking;
    }
}
