package Engine.Components.Graphics;

import Editor.Console;
import Engine.Component;
import Engine.Graphics.RendererType;
import Engine.Graphics.Renderers.Renderer;
import Engine.Graphics.Renderers.Renderers;
import Engine.Graphics.Texture;
import Engine.PerformanceImpact;
import imgui.ImGui;

public class MeshRenderer extends Component {

    private transient Renderer renderer;
    public RendererType renderType;

    public MeshRenderer() {
        icon = new Texture("EngineAssets/Editor/Icons/meshrenderer.png").textureID;
        if (renderType == null) renderType = RendererType.Lit;
        renderer = Renderers.renderers.get(renderType.ordinal());

        RunInEditMode = true;
        description = "Renders mesh data held in MeshFilter component";
        impact = PerformanceImpact.Low;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        renderer.Render(gameObject);
    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void OnVariableUpdate() {
        renderer = Renderers.renderers.get(renderType.ordinal());
    }

    @Override
    public void GUIRender() {

    }

}