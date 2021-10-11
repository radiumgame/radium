package Engine.Graphics.Renderers;

import Engine.Util.NonInstantiatable;

import java.util.ArrayList;
import java.util.List;

public final class Renderers extends NonInstantiatable {

    public static List<Renderer> renderers = new ArrayList<>();

    public static void Initialize() {
        renderers.add(new UnlitRenderer());
        renderers.add(new LitRenderer());
    }

}
