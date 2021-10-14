package Engine.Components.Graphics;

import Engine.Component;
import Engine.Gizmo.ComponentGizmo;
import Engine.Graphics.Material;
import Engine.Graphics.Mesh;
import Engine.Graphics.Shader;
import Engine.Graphics.Texture;
import imgui.ImGui;

public class MeshFilter extends Component {

    public Mesh mesh;

    public Texture texture = new Texture("EngineAssets/Textures/box.jpg");
    public float materialShininess = 1;

    public MeshFilter() {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        mesh = null;

        RunInEditMode = true;
    }

    public MeshFilter(Mesh mesh) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;

        RunInEditMode = true;

        this.mesh.CreateMesh();
    }

    public void SentMaterialToShader(Shader shader) {
        shader.SetUniform("material.reflectivity", materialShininess);
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {

    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void OnVariableUpdate() {
        mesh.material = new Material(texture.filepath);
    }

    @Override
    public void GUIRender() {

    }

}
