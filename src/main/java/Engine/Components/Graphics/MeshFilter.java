package Engine.Components.Graphics;

import Engine.Component;
import Engine.Graphics.Mesh;
import Engine.Graphics.Texture;

public class MeshFilter extends Component {

    public Mesh mesh;

    public MeshFilter() {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        mesh = null;
    }

    public MeshFilter(Mesh mesh) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {

    }

    @Override
    public void GUIRender() {

    }

}
