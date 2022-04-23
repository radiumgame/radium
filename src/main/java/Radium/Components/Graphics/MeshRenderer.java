package Radium.Components.Graphics;

import Radium.Component;
import Radium.Graphics.RendererType;
import Radium.Graphics.Renderers.CustomRenderer;
import Radium.Graphics.Renderers.Renderer;
import Radium.Graphics.Renderers.Renderers;
import Radium.Graphics.Shader;
import Radium.Graphics.Texture;
import Radium.PerformanceImpact;
import Radium.System.FileExplorer;
import Radium.System.Popup;
import Radium.Util.FileUtility;
import RadiumEditor.Annotations.HideInEditor;
import RadiumEditor.Annotations.RunInEditMode;
import RadiumEditor.Console;
import RadiumEditor.EditorWindows.ShaderEditor;
import imgui.ImGui;
import org.lwjgl.opengl.GL11;

import java.io.File;

/**
 * Updates and renders the mesh
 */
@RunInEditMode
public class MeshRenderer extends Component {

    private transient Renderer renderer;
    /**
     * The rendering system to use
     */
    public RendererType renderType = RendererType.Lit;
    private int PreviousRenderType = 1;
    /**
     * If enabled, will cull back faces of object
     */
    public boolean cullFaces = false;

    private static String defaultShader = "#version 330 core\n\nin vec3 vertex_position;\nin vec2 vertex_textureCoord;\nin vec3 vertex_normal;\nout vec4 outColor;\nuniform sampler2D tex;\nuniform vec3 color;\n\nvoid main() {\n   outColor = texture(tex, vertex_textureCoord) * vec4(color, 1.0f);\n}";
    @HideInEditor
    public String shaderPath;

    /**
     * Create empty mesh renderer with default rendering settings
     */
    public MeshRenderer() {
        icon = new Texture("EngineAssets/Editor/Icons/meshrenderer.png").textureID;
        renderer = Renderers.renderers.get(renderType.ordinal());

        name = "Mesh Renderer";
        description = "Renders mesh data held in MeshFilter component";
        impact = PerformanceImpact.Low;
        submenu = "Graphics";
    }

    
    public void Start() {

    }

    
    public void Update() {
        if (cullFaces) GL11.glEnable(GL11.GL_CULL_FACE);
        renderer.Render(gameObject);
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    
    public void Stop() {

    }

    
    public void OnAdd() {

    }

    
    public void OnRemove() {
        if (gameObject.ContainsComponent(Outline.class)) {
            Console.Error("Outline depends on Mesh Renderer");
            gameObject.RemoveComponent(Outline.class);
        }
    }
    
    public void UpdateVariable() {
        if (renderType.ordinal() != PreviousRenderType) {
            if (renderType == RendererType.Custom) {
                // Custom renderer
                boolean create = Popup.YesNo("Would you like to create a new file?");
                String path;
                if (create) path = FileExplorer.Create("glsl");
                else path = FileExplorer.Choose("glsl");
                if (path != null) {
                    if (create) {
                        FileUtility.Create(path);
                        FileUtility.Write(new File(path), defaultShader);
                    }
                    renderer = new CustomRenderer();
                    renderer.shader = new Shader("EngineAssets/Shaders/Lit/vert.glsl", path);
                    shaderPath = path;
                }
            } else {
                renderer = Renderers.renderers.get(renderType.ordinal());
                if (gameObject.ContainsComponent(Outline.class)) {
                    gameObject.GetComponent(Outline.class).shader = Renderers.GetRenderer(renderType).shader;
                }
            }

            PreviousRenderType = renderType.ordinal();
        }
    }

    
    public void GUIRender() {
        if (renderType == RendererType.Custom) {
            if (ImGui.button("Compile Shader")) {
                renderer = new CustomRenderer();
                renderer.shader = new Shader("EngineAssets/Shaders/Lit/vert.glsl", shaderPath);
            }
        }
    }

}