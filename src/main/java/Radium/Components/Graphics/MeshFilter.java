package Radium.Components.Graphics;

import Radium.Application;
import Radium.Component;
import Radium.Graphics.*;

import Radium.Graphics.Shader.Shader;
import Radium.Math.Vector.Vector3;
import Radium.ModelLoader;
import Radium.Objects.GameObject;
import Radium.PerformanceImpact;
import Radium.System.FileExplorer;
import RadiumEditor.Annotations.HideInEditor;
import RadiumEditor.Annotations.RunInEditMode;
import RadiumEditor.Console;
import RadiumEditor.EditorGUI;
import RadiumEditor.MousePicking.MeshCollider;
import imgui.ImGui;

/**
 * Component to load and contain a mesh and material
 */
@RunInEditMode
public class MeshFilter extends Component {

    /**
     * Mesh for the MeshRenderer to load
     */
    public Mesh mesh;
    /**
     * Rendering settings for the renderer to use
     */
    public Material material;

    @HideInEditor
    public boolean selected;

    private transient MeshCollider meshCollider;

    /**
     * Create an empty mesh filter component with no mesh
     */
    public MeshFilter() {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        mesh = null;
        this.material = new Material("EngineAssets/Textures/Misc/blank.jpg");

        name = "Mesh Filter";
        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
        submenu = "Graphics";
    }

    /**
     * Create a mesh filter component with a predefined mesh
     * @param mesh New mesh
     */
    public MeshFilter(Mesh mesh) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;
        this.material = new Material("EngineAssets/Textures/Misc/blank.jpg");

        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
    }

    /**
     * Create a mesh filter component with a predefined mesh and material
     * @param mesh New mesh
     * @param material New material
     */
    public MeshFilter(Mesh mesh, Material material) {
        icon = new Texture("EngineAssets/Editor/Icons/meshfilter.png").textureID;
        this.mesh = mesh;
        this.material = material;

        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
    }

    /**
     * Sends the material variables to the bound shader
     * @param shader Shader to send the variables to
     */
    public void SendMaterialToShader(Shader shader) {
        if (material == null) return;

        shader.SetUniform("specularLighting", material.specularLighting);
        shader.SetUniform("useNormalMap", material.useNormalMap);
        shader.SetUniform("useSpecularMap", material.useSpecularMap);
        shader.SetUniform("material.reflectivity", material.reflectivity);
        shader.SetUniform("material.shineDamper", material.shineDamper);
    }

    private boolean selectedAtRuntime = false;

    
    public void Start() {
        selectedAtRuntime = selected;
        UnSelect();
    }

    
    public void Update() {
        if (!Application.Playing) {
            meshCollider.SetTransform();
        }
    }

    
    public void Stop() {
        if (selectedAtRuntime) {
            Select();
            selectedAtRuntime = false;
        }
    }

    
    public void OnAdd() {
        if (mesh != null) {
            mesh.Destroy();
            mesh.CreateMesh();

            meshCollider = new MeshCollider(gameObject, mesh);
        } else {
            GameObject cube = ModelLoader.LoadModel("EngineAssets/Models/Cube.fbx", false).GetChildren().get(0);
            mesh = cube.GetComponent(MeshFilter.class).mesh;
        }

        if (material != null) {
            material.CreateMaterial();
            material.CreateMaterial();
        }
    }

    
    public void OnRemove() {
        if (mesh != null) {
            mesh.Destroy();
        }
        if (material != null) {
            material.DestroyMaterial();
        }
    }

    
    public void UpdateVariable(String update) {
        if (mesh != null) {
            meshCollider = new MeshCollider(gameObject, mesh);
        }
    }

    @HideInEditor
    public MeshType meshType = MeshType.None;
    public void GUIRender() {
        if (meshType == MeshType.Custom) {
            if (ImGui.button("Choose")) {
                String path = FileExplorer.Choose("fbx,obj;");
                if (path != null) {
                    GameObject model = ModelLoader.LoadModel(path, false);
                    if (model.GetChildren().size() > 1) {
                        Console.Error("Model has more than one child, only the first child will be used");
                    }
                    mesh = model.GetChildren().get(0).GetComponent(MeshFilter.class).mesh;
                } else {
                    mesh = null;
                }
            }
            ImGui.sameLine();
        }
        MeshType t = (MeshType)EditorGUI.EnumSelect("Mesh Type", meshType.ordinal(), MeshType.class);
        if (meshType != t) {
            switch (t) {
                case Cube -> {
                    GameObject cube = ModelLoader.LoadModel("EngineAssets/Models/Cube.fbx", false).GetChildren().get(0);
                    mesh = cube.GetComponent(MeshFilter.class).mesh;
                }
                case Sphere -> {
                    GameObject sphere = ModelLoader.LoadModel("EngineAssets/Models/Sphere.fbx", false).GetChildren().get(0);
                    mesh = sphere.GetComponent(MeshFilter.class).mesh;
                }
                case Plane -> {
                    mesh = Mesh.Plane(1, 1);
                }
                case Custom -> {
                    String path = FileExplorer.Choose("fbx,obj;");
                    GameObject model = ModelLoader.LoadModel(path, false);
                    if (path != null && model != null) {
                        if (model.GetChildren().size() > 1) {
                            Console.Error("Model has more than one child, only the first child will be used");
                        }
                        mesh = model.GetChildren().get(0).GetComponent(MeshFilter.class).mesh;
                    }
                }
                case None -> {
                    mesh = null;
                }
            }

            meshType = t;
        }
    }

    public void SetMeshType(MeshType meshtype) {
        this.meshType = meshtype;
    }

    /**
     * Enables outline in editor game object is selected
     */
    public void Select() {
        if (Application.Playing) return;

        selected = true;
    }

    /**
     * Disables outline in editor
     */
    public void UnSelect() {
        if (Application.Playing) return;

        selected = false;
    }

}
