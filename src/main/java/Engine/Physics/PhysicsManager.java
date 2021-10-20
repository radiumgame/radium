package Engine.Physics;

import Editor.Console;
import Engine.Application;
import Engine.Time;
import Engine.Util.NonInstantiatable;
import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.extensions.PxDefaultAllocator;
import physx.geomutils.PxBoxGeometry;
import physx.physics.*;

public final class PhysicsManager extends NonInstantiatable {

    private static PxDefaultAllocator allocator;
    private static PxDefaultErrorCallback errorCallback;
    private static PxFoundation foundation;
    private static PxTolerancesScale tolerances;
    private static PxPhysics physics;
    private static PxScene scene;

    private static int ThreadCount = 4;
    private static boolean CanUpdate = true;

    public static int PhysxVersion;

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
        if (CanUpdate) scene.simulate(1 / 60f);
        CanUpdate = scene.fetchResults();
    }

    public static PxPhysics GetPhysics() {
        return physics;
    }

    public static PxScene GetPhysicsScene() {
        return scene;
    }

}
