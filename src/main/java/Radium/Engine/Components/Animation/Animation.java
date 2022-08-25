package Radium.Engine.Components.Animation;

import Radium.Editor.Console;
import Radium.Engine.Animation.AnimationClip;
import Radium.Engine.Animation.AnimationKeyframe;
import Radium.Engine.Application;
import Radium.Engine.Component;
import Radium.Engine.Math.Transform;
import Radium.Engine.PerformanceImpact;
import Radium.Engine.Time;
import Radium.Engine.Util.AnimationUtility;

import java.util.ArrayList;

public class Animation extends Component {

    private AnimationClip clip;
    public float animationSpeed = 1;
    public boolean loop = false;

    private float animationTime;
    private float localAnimationTime;
    private int currentKeyframe = 0;
    private boolean restartAnimation;

    public Animation() {
        submenu = "Animation";
        impact = PerformanceImpact.Medium;

        clip = new AnimationClip();

        Transform t1 = new Transform();
        t1.localPosition.z = -1;
        clip.keyframes.add(new AnimationKeyframe(t1, 0));

        Transform t2 = new Transform();
        t2.localPosition.z = 1;
        clip.keyframes.add(new AnimationKeyframe(t2, 1));

        Transform t3 = new Transform();
        t3.localPosition.z = -1;
        clip.keyframes.add(new AnimationKeyframe(t3, 2));
    }

    @Override
    public void Update() {
        if (clip.keyframes.size() == 0) return;

        float step = (1.f / Application.FPS) * animationSpeed;
        animationTime += step;
        localAnimationTime += step;

        int nextIndex = currentKeyframe + 1;
        if (nextIndex >= clip.keyframes.size()) {
            if (loop) {
                currentKeyframe = 0;
                animationTime = 0;
                localAnimationTime = 0;
                restartAnimation = true;
            }
            else {
                nextIndex = currentKeyframe;
            }
        }

        AnimationKeyframe current = clip.keyframes.get(currentKeyframe);
        AnimationKeyframe next = clip.keyframes.get(nextIndex);
        float dst = Math.max(current.position, next.position) - Math.min(current.position, next.position);
        Transform newTransform = AnimationUtility.InterpolateFrames(current, next, localAnimationTime / dst);
        gameObject.transform.localPosition = newTransform.localPosition;
        gameObject.transform.localRotation = newTransform.localRotation;
        gameObject.transform.localScale = newTransform.localScale;

        if (animationTime >= next.position) {
            currentKeyframe++;
            localAnimationTime = 0;
            if (currentKeyframe >= clip.keyframes.size() - 1) {
                if (loop) {
                    currentKeyframe = 0;
                    animationTime = 0;
                    restartAnimation = true;
                }
                else currentKeyframe--;
            }
        }
    }
}
