package Radium.Engine.Physics;

import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.GameObject;

public class RaycastHit {

    public GameObject hit;
    public Vector3 collisionPoint;

    public RaycastHit(GameObject hit, Vector3 collisionPoint) {
        this.hit = hit;
        this.collisionPoint = collisionPoint;
    }

}
