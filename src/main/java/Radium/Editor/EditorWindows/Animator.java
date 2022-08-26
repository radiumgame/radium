package Radium.Editor.EditorWindows;

import Radium.Editor.EditorGUI;
import Radium.Editor.EditorWindow;
import Radium.Engine.Animation.AnimationClip;
import Radium.Engine.Animation.AnimationKeyframe;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.Util.FileUtility;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.io.File;

public class Animator extends EditorWindow {

    private String animationName = "New Animation";
    private AnimationClip currentClip = new AnimationClip();

    public Animator() {
        MenuName = "Animator";
    }

    @Override
    public void Start() {

    }

    @Override
    public void RenderGUI() {
        if (ImGui.treeNodeEx("Animation: " + animationName)) {
            if (ImGui.button("Add")) {
                currentClip.keyframes.add(new AnimationKeyframe());
            }
            ImGui.sameLine();
            if (ImGui.treeNodeEx("Keyframes")) {
                ImGui.indent();
                ImGui.indent();
                for (int i = 0; i < currentClip.keyframes.size(); i++) {
                    RenderKeyframe(currentClip.keyframes.get(i));
                }
                ImGui.unindent();
                ImGui.unindent();

                ImGui.treePop();
            }

            ImGui.treePop();
        }
        if (ImGui.button("Load")) {
            String path = FileExplorer.Choose("anim");
            if (FileExplorer.IsPathValid(path)) {
                currentClip = AnimationClip.Load(path);

                File f = new File(path);
                animationName = f.getName();
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Save")) {
            if (currentClip.path != null) {
                currentClip.Save(currentClip.path);
            } else {
                String savePath = FileExplorer.Create("anim");
                if (FileExplorer.IsPathValid(savePath)) {
                    FileUtility.Create(savePath);
                    currentClip.Save(savePath);
                }
            }
        }
    }

    private void RenderKeyframe(AnimationKeyframe keyframe) {
        if (ImGui.treeNodeEx("Keyframe " + currentClip.keyframes.indexOf(keyframe))) {
            if (ImGui.treeNodeEx("Transform##" + keyframe.hashCode())) {
                keyframe.transform.localPosition = EditorGUI.DragVector3("Position", keyframe.transform.localPosition);
                keyframe.transform.localRotation = EditorGUI.DragVector3("Rotation", keyframe.transform.localRotation);
                keyframe.transform.localScale = EditorGUI.DragVector3("Scale", keyframe.transform.localScale);
                ImGui.treePop();
            }
            keyframe.position = EditorGUI.DragFloat("Time", keyframe.position);

            ImGui.treePop();
        }
    }

}
