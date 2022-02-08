package Radium.Components.Graphics;

import Radium.Color;
import Radium.Component;
import Radium.Graphics.RendererType;
import Radium.Graphics.Renderers.Renderers;
import Radium.Graphics.Shader;
import Radium.Math.Vector.Vector3;
import RadiumEditor.Annotations.RangeFloat;
import RadiumEditor.Annotations.RunInEditMode;

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

    private Shader shader;

    /**
     * Create an empty outline
     */
    public Outline() {
        name = "Outline";

        submenu = "Graphics";
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
        shader = Renderers.GetRenderer(RendererType.Lit).shader;
    }

    @Override
    public void OnRemove() {
        shader.Bind();
        shader.SetUniform("outlineColor", new Vector3(1, 1, 1));
        shader.SetUniform("outlineWidth", 0.25f);
        shader.SetUniform("outline", false);
        shader.Unbind();
    }

    @Override
    public void UpdateVariable() {

    }

    @Override
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
