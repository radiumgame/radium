package RadiumEditor;

import Radium.Scripting.*;
import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImInt;

import java.util.ArrayList;
import java.util.List;

public class NodeScripting {

    public static NodeScript currentScript;
    private static List<NodeInput[]> links = new ArrayList<>();

    protected NodeScripting() {}

    public static void Render() {
        ImGui.begin("Node Scripter");

        if (ImGui.button("Create Node")) {
            ImGui.openPopup("Create Node");
        }
        if (ImGui.button("Create Property")) {
            NodeScriptProperty property = new NodeScriptProperty();
            property.name = "My Float Value";
            property.type = String.class;
            property.value = "My String";

            currentScript.properties.add(property);
        }
        if (ImGui.button("Run")) {
            Run();
        }

        CreatePopup();

        ImGui.beginChildFrame(1, ImGui.getWindowWidth() / 5f, ImGui.getWindowHeight());

        for (NodeScriptProperty property : currentScript.properties) {
            property.Render();
        }
        ImGui.endChildFrame();

        ImGui.sameLine();
        ImNodes.beginNodeEditor();

        int i = 2;
        for (ScriptingNode node : currentScript.nodes) {
            ImNodes.beginNode(i);

            ImNodes.beginNodeTitleBar();
            ImGui.text(node.name);
            ImNodes.endNodeTitleBar();

            for (NodeInput input : node.inputs) {
                ImNodes.beginInputAttribute(input.ID);
                ImGui.text(input.name);
                ImNodes.endInputAttribute();
            }

            node.display.run();

            for (NodeInput output : node.outputs) {
                ImNodes.beginOutputAttribute(output.ID);
                ImGui.text(output.name);
                ImNodes.endOutputAttribute();
            }

            i++;
            ImNodes.endNode();
        }

        for (int l = 0; l < links.size(); l++) {
            ImNodes.link(i, links.get(l)[0].ID, links.get(l)[1].ID);
            i++;
        }

        ImNodes.endNodeEditor();

        if (ImGui.beginDragDropTarget()) {
            if (ImGui.isMouseReleased(0)) {
                NodeScriptProperty property = ImGui.getDragDropPayload(NodeScriptProperty.class);
                currentScript.nodes.add(property.GetNode());
            }

            ImGui.endDragDropTarget();
        }

        ImGui.end();

        ImInt start = new ImInt(0), end = new ImInt(0);
        if (ImNodes.isLinkCreated(start, end)) {
            NodeInput nodeStart = currentScript.GetNodeInputByID(start.get());
            NodeInput nodeEnd = currentScript.GetNodeInputByID(end.get());

            if (nodeStart.type != nodeEnd.type) {
                Console.Error("Types do not match");
                return;
            }

            nodeStart.links.add(nodeEnd);
            nodeEnd.links.add(nodeStart);
            nodeEnd.object = nodeStart.object;
            links.add(new NodeInput[] { nodeStart, nodeEnd });
        }
    }

    private static void CreatePopup() {
        ImGui.setNextWindowSize(200, 350);
        if (ImGui.beginPopup("Create Node")) {
            RenderChoice("Add", NodeType.AddNode());
            RenderChoice("Log", NodeType.Log());

            ImGui.endPopup();
        }
    }

    private static void RenderChoice(String name, ScriptingNode node) {
        if (ImGui.treeNodeEx(name, ImGuiTreeNodeFlags.SpanAvailWidth | ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.DefaultOpen)) {
            if (ImGui.isItemClicked(0)) {
                currentScript.nodes.add(node);
                ImGui.closeCurrentPopup();
            }

            ImGui.treePop();
        }
    }

    private static void Run() {
        ScriptingNode start = currentScript.nodes.get(0);

        TriggerNode(start);
    }

    private static void TriggerNode(ScriptingNode node) {
        node.action.run();

        for (NodeInput link : node.outputs.get(0).links) {
            TriggerNode(link.node);
        }
    }

}
