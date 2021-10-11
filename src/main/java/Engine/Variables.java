package Engine;

import Engine.Components.Rendering.Camera;
import Engine.Objects.EditorCamera;
import Engine.Util.NonInstantiatable;
import Engine.Graphics.Renderers.LitRenderer;

public final class Variables extends NonInstantiatable {

    public static Camera DefaultCamera;
    public static EditorCamera EditorCamera;

}
