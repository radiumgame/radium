package Engine.Components.Graphics;

import Engine.Component;
import Engine.Graphics.Mesh;

public class MeshFilter extends Component {

    public Mesh mesh;

    public MeshFilter() {
        mesh = null;
    }

    public MeshFilter(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {

    }

}
