package Radium.Components.Graphics;

import Radium.Color;
import Radium.Component;
import Radium.Graphics.Renderers.Renderers;
import Radium.Graphics.Shader.Shader;
import Radium.Math.Vector.Vector3;
import Radium.PerformanceImpact;
import RadiumEditor.Annotations.RangeFloat;
import RadiumEditor.Annotations.RunInEditMode;
import RadiumEditor.Console;

/**
 * Draws an outline on the outside of an objects mesh
 */
@RunInEditMode
public class Outline extends Component {

    /**
     * Color of outline
     */
    public Color outlineColor = new Color(1f, 1f, 1f, 1f);

    /**
     * Width of the outline
     */
    @RangeFloat(min = 0.01f, max = 1f)
    public float outlineWidth = 0.25f;

    public Shader shader;

    /**
     * Create an empty outline
     */
    public Outline() {
        LoadIcon("outline.png");
        name = "Outline";

        submenu = "Graphics";
        impact = PerformanceImpact.Low;
        description = "Outlines the object";
    }

    
    public void Start() {

    }

    
    public void Update() {

    }

    
    public void Stop() {

    }

    
    public void OnAdd() {
        if (!gameObject.ContainsComponent(MeshRenderer.class)) {
            Console.Error("Outline requires component Mesh Renderer");
            gameObject.RemoveComponent(Outline.class);
            return;
        }

        shader = Renderers.GetRenderer(gameObject.GetComponent(MeshRenderer.class).renderType).shader;
    }

    
    public void OnRemove() {
        shader.Bind();
        shader.SetUniform("outlineColor", new Vector3(1, 1, 1));
        shader.SetUniform("outlineWidth", 0.25f);
        shader.SetUniform("outline", false);
        shader.Unbind();
    }

    
    public void UpdateVariable() {

    }

    
    public void GUIRender() {

    }

    /**
     * Send uniforms to the bound shader
     */
    public void SendUniforms() {
        shader.SetUniform("outlineColor", outlineColor.ToVector3());
        shader.SetUniform("outlineWidth", outlineWidth);
        shader.SetUniform("outline", true);
    }

}
