package Radium.Scripting;

import Radium.Application;
import Radium.Objects.GameObject;
import Radium.Serialization.TypeAdapters.ClassTypeAdapter;
import Radium.Serialization.TypeAdapters.NodeInputTypeAdapter;
import Radium.System.FileExplorer;
import Radium.Util.FileUtility;
import RadiumEditor.Console;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.extension.imnodes.ImNodes;

import java.io.File;
import java.io.FileWriter;
import java.lang.management.MemoryNotificationInfo;
import java.util.ArrayList;
import java.util.List;

public class NodeScript {

    public List<NodeScriptProperty> properties = new ArrayList<>();
    public List<ScriptingNode> nodes = new ArrayList<>();

    public List<NodeInput[]> links = new ArrayList<>();

    private ScriptingNode start, update;
    public GameObject gameObject;

    public transient String filepath = null;
    public transient String name = null;

    public NodeScript() {
        start = Nodes.Start();
        update = Nodes.Update();

        nodes.add(start);
        nodes.add(update);
    }

    public void Start() {
        for (ScriptingNode node : nodes) {
            for (NodeInput output : node.outputs) {
                output.UpdateLinks();
            }

            node.gameObject = gameObject;
            node.Start(this);
        }

        Run(nodes.get(0));
    }

    public void Update() {
        for (ScriptingNode node : nodes) {
            node.gameObject = gameObject;
            node.Update(this);
        }

        Run(nodes.get(1));
    }

    private void Run(ScriptingNode startNode) {
        TriggerNode(startNode);
    }

    private void TriggerNode(ScriptingNode node) {
        node.action.accept(this);

        NodeInput trigger = node.GetTriggerOutput();
        if (trigger == null) return;

        for (NodeInput link : trigger.links) {
            TriggerNode(link.node);
        }
    }

    public NodeInput GetNodeInputByID(int id) {
        for (ScriptingNode node : nodes) {
            for (NodeInput input : node.inputs) {
                if (input.ID == id) {
                    return input;
                }
            }

            for (NodeInput output : node.outputs) {
                if (output.ID == id) {
                    return output;
                }
            }
        }

        return null;
    }

    public void CreateNode(ScriptingNode node) {
        node.gameObject = gameObject;
        nodes.add(node);
    }

    public void Save() {
        if (filepath == null) {
            filepath = FileExplorer.Create("script");
            if (filepath == null) {
                return;
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Class.class, new ClassTypeAdapter()).registerTypeAdapter(NodeInput.class, new NodeInputTypeAdapter()).create();
        String json = gson.toJson(this);

        try {
            File file = new File(filepath);
            if (!file.exists()) file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public static NodeScript Load(String filepath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Class.class, new ClassTypeAdapter()).registerTypeAdapter(NodeInput.class, new NodeInputTypeAdapter()).create();
        String contents = FileUtility.ReadFile(new File(filepath));
        NodeScript script = gson.fromJson(contents, NodeScript.class);

        for (ScriptingNode node : script.nodes) {
            ImNodes.setNodeGridSpacePos(node.ID, node.position.x, node.position.y);

            node.action = NodeAction.ActionFromType(node);
            node.update = NodeAction.UpdateFromType(node);

            for (NodeInput input : node.inputs) {
                input.node = node;
            }
            for (NodeInput output : node.outputs) {
                output.node = node;
            }
        }
        SetLinks(script);

        script.filepath = filepath;
        script.name = new File(filepath).getName();

        return script;
    }

    private static void SetLinks(NodeScript script) {
        int i = 0;
        for (NodeInput[] links : script.links) {
            NodeInput start = GetNode(links[0].ID, script);
            NodeInput end = GetNode(links[1].ID, script);

            start.links.add(end);
            end.links.add(start);
            start.Link(end);

            NodeInput a = script.links.get(i)[0];
            NodeInput b = script.links.get(i)[1];

            a.node = start.node;
            b.node = end.node;

            i++;
        }
    }

    private static NodeInput GetNode(int id, NodeScript script) {
        for (ScriptingNode node : script.nodes) {
            for (NodeInput input : node.inputs) {
                if (input.ID == id) {
                    return input;
                }
            }
            for (NodeInput output : node.outputs) {
                if (output.ID == id) {
                    return output;
                }
            }
        }

        return null;
    }

}
