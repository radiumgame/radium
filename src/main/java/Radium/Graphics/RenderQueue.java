package Radium.Graphics;

import Radium.Components.Graphics.MeshRenderer;
import Radium.Skybox;

import java.util.ArrayList;
import java.util.List;

public class RenderQueue {

    public static List<MeshRenderer> opaque = new ArrayList<>();
    public static List<MeshRenderer> transparent = new ArrayList<>();

    public static void Render() {
        for (MeshRenderer mr : opaque) {
            mr.Render();
        }
        for (MeshRenderer mr : transparent) {
            mr.Render();
        }
    }

    public static void Clear() {
        opaque.clear();
        transparent.clear();
    }

}
