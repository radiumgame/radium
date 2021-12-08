package Radium.Graphics.Renderers;

import java.util.ArrayList;
import java.util.List;

public class Renderers {

    public static List<Renderer> renderers = new ArrayList<>();

    protected Renderers() {}

    public static void Initialize() {
        renderers.add(new UnlitRenderer());
        renderers.add(new LitRenderer());
    }

}
