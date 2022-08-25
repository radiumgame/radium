package Radium.Engine.Util;

import Radium.Editor.Console;
import Radium.Engine.Animation.AnimationKeyframe;
import Radium.Engine.Math.Transform;
import Radium.Engine.Math.Vector.Vector3;

public class AnimationUtility {

    protected AnimationUtility() {}

    public static Transform InterpolateFrames(AnimationKeyframe current, AnimationKeyframe next, float t) {
        Transform c = current.transform;
        Transform n = next.transform;
        Transform res = new Transform();

        res.localPosition = Lerp(c.localPosition, n.localPosition, t);
        res.localRotation = Lerp(c.localRotation, n.localRotation, t);
        res.localScale = Lerp(c.localScale, n.localScale, t);

        return res;
    }

    private static Vector3 Lerp(Vector3 a, Vector3 b, float t) {
        return new Vector3(Lerp(a.x, b.x, t), Lerp(a.y, b.y, t), Lerp(a.z, b.z, t));
    }

    private static float Lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

}
