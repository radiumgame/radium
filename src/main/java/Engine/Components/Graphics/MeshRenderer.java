package Engine.Components.Graphics;

import Engine.Component;
import Engine.Graphics.Renderer;
import Engine.Graphics.Texture;
import Engine.Variables;

public class MeshRenderer extends Component {

    public MeshRenderer() {
        icon = new Texture("EngineAssets/Editor/Icons/meshrenderer.png").textureID;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        Renderer.Render(gameObject, Variables.DefaultCamera);
    }

    @Override
    public void GUIRender() {

    }

}
