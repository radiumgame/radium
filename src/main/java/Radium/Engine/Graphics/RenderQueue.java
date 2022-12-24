package Radium.Engine.Graphics;

import Radium.Editor.Console;
import Radium.Engine.Components.Graphics.MeshFilter;
import Radium.Engine.Components.Graphics.MeshRenderer;
import Radium.Engine.ParticleSystem.ParticleRenderer;
import Radium.Editor.LocalEditorSettings;
import Radium.Editor.Profiling.Timers;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RenderQueue {

    public static List<MeshRenderer> opaque = new ArrayList<>();
    public static List<MeshRenderer> transparent = new ArrayList<>();
    public static List<ParticleRenderer> opaqueParticles = new ArrayList<>();
    public static List<ParticleRenderer> transparentParticles = new ArrayList<>();

    public static void Render() {
        switch (LocalEditorSettings.ShadeType) {
            case Shaded -> {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                LocalEditorSettings.RenderState = GL11.GL_FILL;
            }
            case Wireframe -> {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                LocalEditorSettings.RenderState = GL11.GL_LINE;
            }
        }
        GL11.glLineWidth(3.5f);

        Timers.StartRenderingTimer();
        for (MeshRenderer mr : opaque) {
            mr.Render();
        }
        for (MeshRenderer mr : transparent) {
            mr.Render();
        }
        Timers.EndRenderingTimer();

        for (ParticleRenderer pr : opaqueParticles) {
            pr.Render();
        }
        for (ParticleRenderer pr : transparentParticles) {
            pr.Render();
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        LocalEditorSettings.RenderState = GL11.GL_FILL;
    }

    public static void Clear() {
        opaque.clear();
        transparent.clear();
        opaqueParticles.clear();
        transparentParticles.clear();
    }

}
