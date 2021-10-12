package Engine.Components.Graphics;

import Engine.Component;
import Engine.Graphics.RendererType;
import Engine.Graphics.Renderers.Renderer;
import Engine.Graphics.Renderers.Renderers;
import Engine.Graphics.Texture;
import imgui.ImGui;

public class MeshRenderer extends Component {

    private transient Renderer renderer;
    public RendererType renderType;

    public MeshRenderer() {
        icon = new Texture("EngineAssets/Editor/Icons/meshrenderer.png").textureID;
        if (renderType == null) renderType = RendererType.Lit;
        renderer = Renderers.renderers.get(renderType.ordinal());

        RunInEditMode = true;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        if (Renderers.renderers.get(renderType.ordinal()) != renderer) {
            renderer = Renderers.renderers.get(renderType.ordinal());
        }

        renderer.Render(gameObject);
    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void GUIRender() {

    }

}