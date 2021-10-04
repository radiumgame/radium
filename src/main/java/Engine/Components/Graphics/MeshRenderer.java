package Engine.Components.Graphics;

import Engine.Component;
import Engine.Graphics.Renderers.LitRenderer;
import Engine.Graphics.Renderers.Renderer;
import Engine.Graphics.Renderers.UnlitRenderer;
import Engine.Graphics.Texture;
import Engine.Variables;

public class MeshRenderer extends Component {

    private transient Renderer renderer;

    public MeshRenderer() {
        icon = new Texture("EngineAssets/Editor/Icons/meshrenderer.png").textureID;
        renderer = Variables.LitRenderer;
    }
    public MeshRenderer(Renderer renderer) {
        icon = new Texture("EngineAssets/Editor/Icons/meshrenderer.png").textureID;
        this.renderer = renderer;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        renderer.Render(gameObject);
    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void GUIRender() {

    }

}
