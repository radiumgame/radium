package Radium.Graphics;

import Radium.Components.Graphics.MeshRenderer;
import Radium.Skybox;
import RadiumEditor.LocalEditorSettings;
import RadiumEditor.RenderMode;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RenderQueue {

    public static List<MeshRenderer> opaque = new ArrayList<>();
    public static List<MeshRenderer> transparent = new ArrayList<>();

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
            case ShadedWireframe -> {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                LocalEditorSettings.RenderState = GL11.GL_LINE;
            }
        }
        GL11.glLineWidth(3.5f);

        for (MeshRenderer mr : opaque) {
            mr.Render();
        }
        for (MeshRenderer mr : transparent) {
            mr.Render();
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        LocalEditorSettings.RenderState = GL11.GL_FILL;
        if (LocalEditorSettings.ShadeType == RenderMode.ShadedWireframe) {
            for (MeshRenderer mr : opaque) {
                mr.Render();
            }
            for (MeshRenderer mr : transparent) {
                mr.Render();
            }
        }
    }

    public static void Clear() {
        opaque.clear();
        transparent.clear();
    }

}
