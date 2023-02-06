package Radium.Engine.Components.Animation;

import Radium.Editor.Annotations.HideInEditor;
import Radium.Editor.Console;
import Radium.Engine.Animation.AnimationClip;
import Radium.Engine.Animation.AnimationKeyframe;
import Radium.Engine.Application;
import Radium.Engine.Component;
import Radium.Engine.Math.Transform;
import Radium.Engine.PerformanceImpact;
import Radium.Engine.Time;
import Radium.Engine.Util.AnimationUtility;
import Radium.Integration.Project.Project;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Animation extends Component {

    @JsonIgnore
    public AnimationClip clip;
    @HideInEditor
    public String clipPath;

    public float animationSpeed = 1;
    public boolean loop = false;
    public boolean playOnAwake = true;

    private float animationTime;
    private float localAnimationTime;
    private int currentKeyframe = 0;
    private boolean playing = false;

    public Animation() {
        submenu = "Animation";
        impact = PerformanceImpact.Medium;

        LoadIcon("animation.png");

        clip = new AnimationClip();
    }

    @Override
    public void Start() {
        playing = playOnAwake;
    }

    @Override
    public void Update() {
        if (clip.keyframes.size() == 0 || !playing) return;

        float step = (1.f / Application.FPS) * animationSpeed;
        animationTime += step;
        localAnimationTime += step;

        int nextIndex = currentKeyframe + 1;
        if (nextIndex >= clip.keyframes.size()) {
            if (loop) {
                currentKeyframe = 0;
                animationTime = 0;
                localAnimationTime = 0;
            }
            else {
                currentKeyframe = 0;
                localAnimationTime = 0;
                animationTime = 0;
                playing = false;
                return;
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
            if (currentKeyframe >= clip.keyframes.size() - 1 && loop) {
                currentKeyframe = 0;
                animationTime = 0;
            }
        }
    }

    @Override
    public void EditorUpdate() {
        if (clip == null) {
            clip = new AnimationClip();
        }

        if (clipPath == null && clip.path != null) {
            clipPath = clip.path;
        }
    }

    @Override
    public void OnAdd() {
        if (clipPath != null) {
            clip = AnimationClip.Load(clipPath);
        }
    }

    @Override
    public void UpdateVariable(String variableName) {
        if (DidFieldChange(variableName, "clip")) {
            clipPath = clip.path;
        }
    }

    public void Play() {
        playing = true;
    }

    public void Stop() {
        playing = false;
        currentKeyframe = 0;
        animationTime = 0;
        localAnimationTime = 0;
    }

    public void Pause() {
        playing = false;
    }

    public void SetAnimationClip(String clipPath, boolean fromAssets) {
        if (clipPath == null || clipPath.isEmpty()) {
            return;
        }

        String path = (fromAssets ? Project.Current().assets + "/" : "") + clipPath;
        if (!Files.exists(Paths.get(path))) {
            Console.Error("Invalid animation path");
            return;
        }

        clip = AnimationClip.Load(path);
    }

}
