package Radium.Engine.Components.Scripting;

import Radium.Editor.Annotations.HideInEditor;
import Radium.Editor.Console;
import Radium.Editor.Files.Parser;
import Radium.Engine.Component;
import Radium.Engine.Scripting.Node.NodeGraph;
import Radium.Engine.Util.FileUtility;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;

public class NodeScripting extends Component {

    @JsonIgnore
    public NodeGraph graph;

    @HideInEditor
    public File graphPath;

    public NodeScripting() {
        Hide = true;
        LoadIcon("node_scripting.png");
    }

    public NodeScripting(File script) {
        name = FileUtility.NameWithoutExtension(script);
        Hide = true;
        LoadIcon("node_scripting.png");
        graphPath = script;
    }

    @Override
    public void Start() {
        graph.GetEvents();
        graph.Start();
    }

    @Override
    public void Update() {
        graph.Update();
    }

    @Override
    public void OnAdd() {
        if (graphPath != null) {
            graph = NodeGraph.Load(graphPath.getAbsolutePath(), gameObject);
            name = FileUtility.NameWithoutExtension(graphPath);
        } else {
            gameObject.RemoveComponent(NodeScripting.class);
        }
    }

    @Override
    public void GUIRender() {
        if (graph == null) return;

        graph.GetProperties().forEach((property -> {
            property.RenderChangeValueWithName();
        }));
    }

}
