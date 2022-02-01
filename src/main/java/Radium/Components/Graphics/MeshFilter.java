package Radium.Components.Graphics;

import Radium.Component;
import Radium.Graphics.Material;
import Radium.Graphics.Mesh;
import Radium.Graphics.Shader;
import Radium.Graphics.Texture;
import Radium.PerformanceImpact;

public class MeshFilter extends Component {

    public Mesh mesh;
    public Material material;

    public MeshFilter() {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        mesh = null;
        this.material = new Material("EngineAssets/Textures/Misc/blank.jpg");

        RunInEditMode = true;
        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
        submenu = "Graphics";
    }

    public MeshFilter(Mesh mesh) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;
        this.material = new Material("EngineAssets/Textures/Misc/blank.jpg");

        RunInEditMode = true;
        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
    }

    public MeshFilter(Mesh mesh, Material material) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;
        this.material = material;

        RunInEditMode = true;
        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
    }

    public void SendMaterialToShader(Shader shader) {
        if (material == null) return;

        shader.SetUniform("useNormalMap", material.useNormalMap);
        shader.SetUniform("material.reflectivity", material.reflectivity);
        shader.SetUniform("material.shineDamper", material.shineDamper);
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {

    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {
        if (mesh != null) {
            mesh.Destroy();
            mesh.CreateMesh();
        }

        if (material != null) {
            material.CreateMaterial();
            material.CreateMaterial();
        }
    }

    @Override
    public void OnRemove() {
        if (mesh != null) {
            mesh.Destroy();
        }
        if (material != null) {
            material.DestroyMaterial();
        }
    }

    @Override
    public void UpdateVariable() {

    }

    @Override
    public void GUIRender() {

    }

}
