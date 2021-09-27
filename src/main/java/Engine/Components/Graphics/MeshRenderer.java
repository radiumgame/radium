package Engine.Components.Graphics;

import Engine.Component;
import Engine.Graphics.Renderer;
import Engine.Variables;

public class MeshRenderer extends Component {

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        Renderer.Render(gameObject, Variables.DefaultCamera);
    }

}
