package Radium.Engine.Animation;

import Radium.Engine.Math.Transform;

public class AnimationKeyframe {

    public Transform transform;
    public float position;

    public AnimationKeyframe() {
        transform = new Transform();
        position = 0;
    }

    public AnimationKeyframe(Transform transform, float position) {
        this.transform = transform;
        this.position = position;
    }

}
