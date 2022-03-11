package Radium.Components.UI;

import Radium.Color;
import Radium.Component;
import Radium.Math.Mathf;
import Radium.Math.Vector.Vector2;
import Radium.UI.Text.CFont;
import Radium.UI.UIMesh;
import Radium.UI.UIRenderer;
import RadiumEditor.Annotations.RangeFloat;
import RadiumEditor.Annotations.RangeInt;
import RadiumEditor.Console;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class Text extends Component {

    /**
     * The texts position
     */
    public Vector2 Position = Vector2.Zero();
    /**
     * The display text
     */
    public String text = "Placeholder text";
    /**
     * Font size of text
     */
    @RangeInt(min = 1, max = 256)
    public int fontSize = 64;
    /**
     * Color of text
     */
    public Color color = new Color(1f, 1f, 1f, 1f);

    private transient List<UIMesh> characters = new ArrayList<>();
    private transient CFont font;

    /**
     * Create empty text component
     */
    public Text() {
        LoadIcon("text.png");
        submenu = "UI";
    }

    /**
     * Create text component with predefined text
     * @param text The display text
     */
    public Text(String text) {
        LoadIcon("text.png");
        this.text = text;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        for (UIMesh mesh : characters) {
            UIRenderer.Render(mesh);
        }
    }

    @Override
    public void Stop() {

    }

    @Override
    public void OnAdd() {
        font = new CFont("C:/Windows/Fonts/Arial.ttf", fontSize);
        CreateMeshes();
    }

    @Override
    public void OnRemove() {

    }

    @Override
    public void UpdateVariable() {
        float addWidth = 0;
        for (UIMesh character : characters) {
            character.color = color;

            character.Position = Vector2.Add(Position, new Vector2(addWidth, 0));
            addWidth += character.Size.x;

            if (character.Size.x <= 36) {
                //character.Position.x -= 16;
            }
        }
    }

    @Override
    public void GUIRender() {
        if (ImGui.button("Create Font + Mesh")) {
            font = new CFont("C:/Windows/Fonts/Arial.ttf", fontSize);
            CreateMeshes();
        }
    }

    private void CreateMeshes() {
        characters.clear();

        float xPos = Position.x;
        for (char character : text.toCharArray()) {
            UIMesh charMesh = UIMesh.Character(font, font.GetCharacter(character));
            charMesh.Position.x = xPos;

            characters.add(charMesh);
            xPos += font.GetCharacter(character).width;
        }
    }

}
