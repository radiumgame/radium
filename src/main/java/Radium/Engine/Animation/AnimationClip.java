package Radium.Engine.Animation;

import Radium.Editor.Console;
import Radium.Engine.Util.FileUtility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AnimationClip {

    @JsonIgnore
    public String path = null;
    public List<AnimationKeyframe> keyframes = new ArrayList<>();

    public void Save(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            String json = mapper.writeValueAsString(this);
            FileUtility.Write(new File(path), json);
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public void LoadProperties(String path) {
        AnimationClip clip = Load(path);
        if (clip == null) return;
        keyframes = clip.keyframes;
    }

    public static AnimationClip Load(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
            String json = FileUtility.ReadFile(new File(path));
            AnimationClip clip = (AnimationClip) mapper.readValue(json, AnimationClip.class);
            clip.path = path;
            return clip;
        } catch (Exception e) {
            Console.Error(e);
        }

        return null;
    }

}
