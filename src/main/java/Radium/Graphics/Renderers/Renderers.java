package Radium.Graphics.Renderers;

import Radium.Graphics.RendererType;

import java.util.ArrayList;
import java.util.List;

/**
 * List of renders the engine can use
 */
public class Renderers {

    /**
     * List of renders
     */
    public static List<Renderer> renderers = new ArrayList<>();

    protected Renderers() {}

    /**
     * Initialize usable renderers
     */
    public static void Initialize() {
        renderers.add(new UnlitRenderer());
        renderers.add(new LitRenderer());
    }

    /**
     * Returns renderer based on renderer type
     * @param rendererType Renderer type
     * @return Renderer
     */
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
