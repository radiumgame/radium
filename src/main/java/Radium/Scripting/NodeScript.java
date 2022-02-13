package Radium.Scripting;

import Radium.System.FileExplorer;
import RadiumEditor.Console;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class NodeScript {

    public List<NodeScriptProperty> properties = new ArrayList<>();
    public List<ScriptingNode> nodes = new ArrayList<>();

    public String filepath = null;

    public NodeScript() {
        nodes.add(NodeType.Start());
        // nodes.add(NodeType.Update());
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

    public void Save() {
        if (filepath == null) {
            filepath = FileExplorer.Create("script");
            if (filepath == null) {
                return;
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);

        try {
            File file = new File(filepath);
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

}
