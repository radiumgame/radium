package Radium.Graphics.Renderers;

import Radium.Graphics.RendererType;

import java.util.ArrayList;
import java.util.List;

public class Renderers {

    public static List<Renderer> renderers = new ArrayList<>();

    protected Renderers() {}

    public static void Initialize() {
        renderers.add(new UnlitRenderer());
        renderers.add(new LitRenderer());
    }

    public static Renderer GetRenderer(RendererType rendererType) {
        switch (rendererType) {
            case Unlit -> {
                return renderers.get(0);
            }
            case Lit -> {
                return renderers.get(1);
            }
            default -> {
                return GetRenderer(RendererType.Lit);
            }
        }
    }

}
