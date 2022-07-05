package Radium.Engine.Components.Graphics;

import Radium.Engine.Application;
import Radium.Engine.Component;
import Radium.Engine.Graphics.*;

import Radium.Engine.Graphics.Lighting.LightCalculationMode;
import Radium.Engine.Graphics.Shader.Shader;
import Radium.Engine.ModelLoader;
import Radium.Engine.Objects.GameObject;
import Radium.Engine.PerformanceImpact;
import Radium.Engine.System.FileExplorer;
import Radium.Engine.System.Popup;
import Radium.Engine.Util.ThreadUtility;
import Radium.Editor.Annotations.HideInEditor;
import Radium.Editor.Annotations.RunInEditMode;
import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
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
    public transient boolean selected;

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
        name = "Mesh Filter";

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
        name = "Mesh Filter";

        description = "Stores mesh data for renderers to render";
        impact = PerformanceImpact.Low;
    }

    /**
     * Sends the material variables to the bound shader
     * @param shader Shader to send the variables to
     */
    public void SendMaterialToShader(Shader shader) {
        if (material == null) return;
        if (material.lightCalculationMode == null) {
            material.lightCalculationMode = LightCalculationMode.Normal;
        }

        shader.SetUniform("specularLighting", material.specularLighting);
        shader.SetUniform("useNormalMap", material.useNormalMap);
        shader.SetUniform("useSpecularMap", material.useSpecularMap);
        shader.SetUniform("lightCalcMode", material.lightCalculationMode.ordinal());

        shader.SetUniform("material.reflectivity", material.reflectivity);
        shader.SetUniform("material.shineDamper", material.shineDamper);

        shader.SetUniform("material.metallic", material.metallic);
        shader.SetUniform("material.alpha", material.glossiness);
        shader.SetUniform("material.baseReflectivity", material.fresnel);
    }

    private boolean selectedAtRuntime = false;

    
    public void Start() {
        selectedAtRuntime = selected;
        UnSelect();
    }

    
    public void Update() {

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
        if (update.equals("meshType")) {
            switch (meshType) {
                case Cube -> {
                    GameObject cube = ModelLoader.LoadModel("EngineAssets/Models/Cube.fbx", false).GetChildren().get(0).GetChildren().get(0);
                    mesh = cube.GetComponent(MeshFilter.class).mesh;
                }
                case Sphere -> {
                    GameObject sphere = ModelLoader.LoadModel("EngineAssets/Models/Sphere.fbx", false).GetChildren().get(0).GetChildren().get(0);
                    mesh = sphere.GetComponent(MeshFilter.class).mesh;
                }
                case Plane -> {
                    mesh = Mesh.Plane(1, 1);
                }
                case None -> {
                    mesh = null;
                }
            }
        }
    }

    @HideInEditor
    public MeshType meshType = MeshType.None;
    public void GUIRender() {
        if (meshType == MeshType.Custom) {
            if (ImGui.button("Choose")) {
                String path = FileExplorer.Choose("fbx,obj,gltf;");
                if (path != null) {
                    boolean textures = Popup.YesNo("Would you like to load textures(longer wait time)?");
                    ThreadUtility.Run(() -> {
                        GameObject model = ModelLoader.LoadModel(path, false, textures);
                        if (model.GetChildren().size() > 1) {
                            Console.Error("Model has more than one child, only the first child will be used");
                        }
                        mesh = model.GetChildren().get(0).GetComponent(MeshFilter.class).mesh;
                    });
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
                    GameObject cube = ModelLoader.LoadModel("EngineAssets/Models/Cube.fbx", false).GetChildren().get(0).GetChildren().get(0);
                    mesh = cube.GetComponent(MeshFilter.class).mesh;
                }
                case Sphere -> {
                    GameObject sphere = ModelLoader.LoadModel("EngineAssets/Models/Sphere.fbx", false).GetChildren().get(0).GetChildren().get(0);
                    mesh = sphere.GetComponent(MeshFilter.class).mesh;
                }
                case Plane -> {
                    mesh = Mesh.Plane(1, 1);
                }
                case Custom -> {
                    String path = FileExplorer.Choose("fbx,obj,gltf;");
                    boolean textures = Popup.YesNo("Would you like to load textures(longer wait time)?");

                    ThreadUtility.Run(() -> {
                        GameObject model = ModelLoader.LoadModel(path, false, textures);
                        if (path != null && model != null) {
                            if (model.GetChildren().size() > 1) {
                                Console.Error("Model has more than one child, only the first child will be used");
                            }
                            mesh = model.GetChildren().get(0).GetComponent(MeshFilter.class).mesh;
                        }
                    });
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
