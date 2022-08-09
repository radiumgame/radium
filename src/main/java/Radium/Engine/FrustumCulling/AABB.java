package Radium.Engine.FrustumCulling;

import Radium.Engine.Math.Vector.Vector3;

public class AABB {

    public Vector3 min, max;

    public AABB(Vector3 min, Vector3 max) {
        this.min = min;
        this.max = max;
    }

}
