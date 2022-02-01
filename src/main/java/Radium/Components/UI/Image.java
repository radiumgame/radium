package Radium.Components.UI;

import Radium.Color;
import Radium.Component;
import Radium.Graphics.Texture;
import Radium.Math.Vector.Vector3;
import Radium.UI.UIMesh;
import Radium.UI.UIRenderer;
import RadiumEditor.EditorGUI;

public class Image extends Component {

    private UIMesh mesh;

    public Image() {
        submenu = "UI";

        mesh = UIMesh.Quad();
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        UIRenderer.Render(mesh, gameObject.transform);
    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {

    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void UpdateVariable() {

    }

    @Override
    public void GUIRender() {
        Texture newTex = EditorGUI.TextureField(mesh.texture);
        if (newTex != null) {
            mesh.texture = newTex;
        }

        mesh.color = EditorGUI.ColorField("Color", mesh.color);
    }

}
