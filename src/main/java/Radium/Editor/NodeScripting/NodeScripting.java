package Radium.Editor.NodeScripting;

import Radium.Editor.Console;
import Radium.Editor.EditorGUI;
import Radium.Editor.Icons;
import Radium.Engine.Input.Input;
import Radium.Engine.Input.Keys;
import Radium.Engine.Math.Vector.Vector2;
import Radium.Engine.Scripting.Node.*;
import Radium.Engine.Scripting.Node.Events.Link;
import Radium.Engine.Scripting.Node.IO.NodeIO;
import Radium.Engine.Scripting.Node.IO.NodeInput;
import Radium.Engine.Scripting.Node.IO.NodeOutput;
import Radium.Engine.Scripting.Node.Properties.Property;
import Radium.Engine.System.FileExplorer;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.ImNodesContext;
import imgui.extension.imnodes.ImNodesStyle;
import imgui.extension.imnodes.flag.ImNodesColorStyle;
import imgui.extension.imnodes.flag.ImNodesPinShape;
import imgui.extension.imnodes.flag.ImNodesStyleFlags;
import imgui.extension.imnodes.flag.ImNodesStyleVar;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class NodeScripting  {

    public static boolean Render = false;
    public static boolean FocusingEditor = false;

    // Link creation
    private static final ImInt startNode = new ImInt(0), endNode = new ImInt(0), startAttribute = new ImInt(0), endAttribute = new ImInt(0);
    private static final ImInt droppedLink = new ImInt(0);
    private static int hoveredPin = -1;

    private static final ImNodesContext nodeEditor = new ImNodesContext();
    public static NodeGraph graph = NodeGraph.BasicGraph();

    private static final Vector2 mousePositionEditorSpace = new Vector2(0, 0);
    private static final Vector2 droppedMousePosition = new Vector2(0, 0);

    private static String search = "";
    private static final float NodeInnerPadding = 20;

    protected NodeScripting() {}

    public static void Initialize() {
        ImNodes.createContext();
        Nodes.GetIcons();
        NodeScriptingData.Initialize();
        SetTheme();
        UpdateProperties();
    }

    public static void Render() {
        if (!Render) return;
        if (graph == null) graph = NodeGraph.BasicGraph();

        ImGui.begin("Node Scripting", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar);

        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Open")) {
                    String path = FileExplorer.Choose("graph");
                    if (FileExplorer.IsPathValid(path)) {
                        graph = NodeGraph.Load(path, null);
                        UpdateProperties();
                    }
                }
                if (ImGui.menuItem("Save")) {
                    if (graph.path == null) {
                        String path = FileExplorer.Create("graph");
                        if (FileExplorer.IsPathValid(path)) graph.Save(path);
                    } else {
                        graph.Save(graph.path);
                    }
                }

                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }

        ColorTheme.forEach(ImNodes::pushColorStyle);

        ImVec2 pan = new ImVec2();
        ImNodes.editorContextGetPanning(pan);
        Vector2 mouse = Input.GetMousePosition();
        Vector2 editorPosition = new Vector2(ImGui.getWindowPosX() + 7, ImGui.getWindowPosY() + 2);
        Vector2 editorSize = new Vector2(ImGui.getWindowSizeX() - 10, ImGui.getWindowSizeY() - 10);
        float x = InverseLerp(editorPosition.x, editorPosition.x + editorSize.x, 0, editorSize.x, mouse.x);
        float y = InverseLerp(editorPosition.y, editorPosition.y + editorSize.y, 0, editorSize.y, mouse.y);
        mousePositionEditorSpace.x = x;
        mousePositionEditorSpace.y = y;

        ImNodes.editorContextSet(nodeEditor);
        ImNodes.beginNodeEditor();

        ImGui.setCursorScreenPos(ImGui.getWindowPosX(), ImGui.getWindowPosY());
        if (ImGui.beginChildFrame(120, ImGui.getWindowWidth(), ImGui.getWindowHeight() - 15, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoMouseInputs)) {
            ImGui.endChildFrame();
            if (ImGui.beginDragDropTarget()) {
                if (ImGui.isMouseReleased(0)) {
                    Object payload = ImGui.getDragDropPayload();
                    if (payload.getClass().isAssignableFrom(Property.class)) {
                        Node newNode = graph.CreateNode(((Property)payload).CreateNode());
                        newNode.SetPosition(mousePositionEditorSpace.x, mousePositionEditorSpace.y - 55);
                    }
                }

                ImGui.endDragDropTarget();
            }
        }

        for (Node node : graph.GetNodes()) {
            ImNodes.beginNode(node.id);

            if (node.NeedSetPosition) {
                node.UpdatePosition();
                node.NeedSetPosition = false;
            }

            ImNodes.beginNodeTitleBar();
            ImGui.text(node.name);
            ImNodes.endNodeTitleBar();

            for (int i = 0; i < Math.max(node.inputs.size(), node.outputs.size()); i++) {
                boolean inputAvailable = node.inputs.size() > i;
                boolean outputAvailable = node.outputs.size() > i;

                if (inputAvailable) {
                    NodeInput input = node.inputs.get(i);
                    int shape = input.link != null ? ImNodesPinShape.CircleFilled : ImNodesPinShape.Circle;
                    ImNodes.beginInputAttribute(input.id, shape);
                    if (hoveredPin == input.id) {
                        ImGui.beginTooltip();
                        ImGui.text(input.type.name);
                        ImGui.endTooltip();
                    }

                    if (input.icon != -1) {
                        ImGui.image(input.icon, 15, 15);
                        ImGui.sameLine();
                    }

                    if (input.showName) ImGui.text(input.name);
                    ImNodes.endInputAttribute();

                    if (outputAvailable) ImGui.sameLine();
                }

                boolean space = node.inputs.size() > 0;
                if (space) {
                    ImGui.beginChildFrame(i + 1, NodeInnerPadding, 5, ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoDecoration);
                    ImGui.endChildFrame();
                    if (outputAvailable) ImGui.sameLine();
                }

                if (outputAvailable) {
                    NodeOutput output = node.outputs.get(i);
                    int shape = output.links.size() > 0 ? ImNodesPinShape.CircleFilled : ImNodesPinShape.Circle;
                    ImNodes.beginOutputAttribute(output.id, shape);
                    if (hoveredPin == output.id) {
                        ImGui.beginTooltip();
                        ImGui.text(output.type.name);
                        ImGui.endTooltip();
                    }

                    if (space) ImGui.setCursorScreenPos(ImGui.getCursorScreenPosX() + NodeInnerPadding, ImGui.getCursorScreenPosY());

                    if (output.icon != -1) {
                        ImGui.image(output.icon, 15, 15);
                        ImGui.sameLine();
                    }

                    if (output.showName) ImGui.text(output.name);
                    ImNodes.endOutputAttribute();
                }
            }

            node.gui.Call();

            ImNodes.endNode();
        }

        for (Link link : graph.links) {
            ImNodes.link(link.id, link.startIo, link.endIo);
        }

        ImNodes.endNodeEditor();
        hoveredPin = ImNodes.getHoveredPin();

        ColorTheme.forEach((flag, color) -> ImNodes.popColorStyle());

        if (ImNodes.isLinkCreated(startNode, startAttribute, endNode, endAttribute, new ImBoolean())) {
            Node start = Node.GetNode(startNode.get());
            NodeOutput output = (NodeOutput) NodeIO.GetIO(startAttribute.get());
            Node end = Node.GetNode(endNode.get());
            NodeInput input = (NodeInput) NodeIO.GetIO(endAttribute.get());

            boolean canLink = input.type.CanLink(output.type);
            if (canLink) {
                if (input.link != null) {
                    DestroyLink(input.link.id);
                }

                start.OnLink(end, output);
                end.OnLink(start, input);

                Link newLink = new Link(start.id, end.id, output.id, input.id, graph);
                input.link = newLink;
                output.links.add(newLink);
            }
        }
        if (Input.GetKeyPressed(Keys.Delete)) {
            int[] destroyedLinks = new int[ImNodes.numSelectedLinks()];
            ImNodes.getSelectedLinks(destroyedLinks);

            int[] destroyedNodes = new int[ImNodes.numSelectedNodes()];
            ImNodes.getSelectedNodes(destroyedNodes);

            for (int i = 0; i < destroyedLinks.length; i++) {
                int destroyedLink = destroyedLinks[i];

                graph.links.remove(Link.GetLinks(destroyedLink));
                DestroyLink(destroyedLink);
            }

            for (int i = 0; i < destroyedNodes.length; i++) {
                int destroyedNode = destroyedNodes[i];
                Node node = Node.GetNode(destroyedNode);

                for (NodeInput input : node.inputs) {
                    Link link = input.link;
                    if (link != null) {
                        graph.links.remove(link);
                        DestroyLink(link.id);
                    }
                }

                for (NodeOutput output : node.outputs) {
                    for (Link link : output.links) {
                        if (link != null) {
                            graph.links.remove(link);
                            DestroyLink(link.id);
                        }
                    }
                }

                graph.DestroyNode(node);
                Node.DestroyNode(node.id);
            }
        }

        DrawPropertiesEditor();

        ImNodes.editorContextGetPanning(currentPan);

        ImVec2 drag = ImGui.getMouseDragDelta(1);
        ImNodes.editorResetPanning(currentPan.x + drag.x, currentPan.y + drag.y);
        ImGui.resetMouseDragDelta(1);

        if (!ImGui.isMouseDragging(1)) {
            boolean mouseClicked = ImGui.isMouseReleased(1) && CanOpenMenu;
            if (mouseClicked || ImNodes.isLinkDropped(droppedLink, false)) {
                OpenedWithClick = mouseClicked;

                droppedMousePosition.x = mousePositionEditorSpace.x;
                droppedMousePosition.y = mousePositionEditorSpace.y;
                ImGui.openPopup("Create Node");
            }
        }
        RenderAddMenu(droppedLink.get());

        FocusingEditor = ImNodes.isEditorHovered();
        CanOpenMenu = ImGui.isWindowHovered() || ImNodes.isEditorHovered();
        ImGui.end();
    }

    private static ImVec2 currentPan = new ImVec2();

    private static boolean CanOpenMenu = false;
    private static boolean OpenedWithClick = true;
    private static final int CreateTreeNodeFlags = ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.FramePadding;
    private static final int AddTreeNodeFlags = ImGuiTreeNodeFlags.SpanFullWidth | ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.FramePadding;
    private static void RenderAddMenu(int droppedLink) {
        ImGui.setNextWindowSize(250, 300);
        if (ImGui.beginPopup("Create Node", ImGuiWindowFlags.NoResize)) {
            try {
                float xAvail = ImGui.getContentRegionAvailX();

                ImGui.image(Icons.GetIcon("search"), 17, 17);
                ImGui.setCursorScreenPos(ImGui.getCursorScreenPosX() + 22, ImGui.getCursorScreenPosY() - 25);
                ImGui.setNextItemWidth(xAvail - 27);
                String newSearch = EditorGUI.InputString("##ADD_NODE_SEARCH", search);
                if (!newSearch.equals(search)) {
                    search = newSearch;
                    NodeScriptingData.Filter(search);
                }

                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 5);

                if (!NodeScriptingData.Filtering) {
                    NodeScriptingData.allNodes.forEach((submenu, items) -> {
                        if (ImGui.treeNodeEx(submenu, CreateTreeNodeFlags)) {
                            items.forEach((item) -> {
                                try {
                                    RenderNodeOption(item, OpenedWithClick, droppedLink);
                                } catch (Exception e) {
                                    Console.Error(e);
                                }
                            });

                            ImGui.treePop();
                        }
                    });
                } else {
                    NodeScriptingData.filtered.forEach((item) -> {
                        try {
                            RenderNodeOption(item, OpenedWithClick, droppedLink);
                        } catch (Exception e) {
                            Console.Error(e);
                        }
                    });
                }

                ImGui.popStyleVar();
            } catch (Exception e) {
                Console.Error(e);
            }

            ImGui.endPopup();
        }
    }

    private static void RenderNodeOption(String name, boolean mouseClick, int droppedLink) throws Exception {
        Method createMethod = GetNode(name.replaceAll(" ", ""));

        final float padding = 15;
        ImVec2 ccp = ImGui.getCursorScreenPos();
        ImGui.setCursorScreenPos(ccp.x + padding, ccp.y - 1.5f);

        if (ImGui.treeNodeEx(name, AddTreeNodeFlags)) {
            ImGui.treePop();
        }
        if (ImGui.isItemClicked()) {
            Node newNode = graph.CreateNode(((Node)createMethod.invoke(null)).SetPosition(droppedMousePosition.x, droppedMousePosition.y));
            if (!mouseClick) {
                NodeIO nodeIO = NodeIO.GetIO(droppedLink);
                Node parent = Node.GetNode(nodeIO.parentId);
                if (nodeIO.isOutput) {
                    for (NodeInput input : newNode.inputs) {
                        if (input.type.CanLink(nodeIO.type)) {
                            Node start = parent;
                            Node end = newNode;

                            start.OnLink(end, input);
                            end.OnLink(start, nodeIO);

                            Link newLink = new Link(start.id, end.id, nodeIO.id, input.id, graph);
                            input.link = newLink;
                            ((NodeOutput)nodeIO).links.add(newLink);

                            break;
                        }
                    }
                } else {
                    for (NodeOutput output : newNode.outputs) {
                        if (output.type.CanLink(nodeIO.type)) {
                            Node start = newNode;
                            Node end = parent;

                            start.OnLink(end, nodeIO);
                            end.OnLink(start, output);

                            Link newLink = new Link(start.id, end.id, output.id, nodeIO.id, graph);
                            ((NodeInput)nodeIO).link = newLink;
                            output.links.add(newLink);

                            break;
                        }
                    }
                }
            }

            ImGui.closeCurrentPopup();
            OpenedWithClick = true;
        }

        int icon = Nodes.GetIcon(name);

        if (icon != -1) {
            ImVec2 ccp2 = ImGui.getCursorScreenPos();
            ImGui.setCursorScreenPos(ccp.x, ccp.y);
            ImGui.image(icon, 25, 25);
            ImGui.setCursorScreenPos(ccp2.x, ccp2.y);
        }
    }

    private static List<Property> propertiesList = new ArrayList<>();
    private static void DrawPropertiesEditor() {
        ImVec2 windowPos = ImGui.getWindowPos();
        ImGui.setCursorScreenPos(windowPos.x + 20, windowPos.y + 65);

        ImVec4 themeColor = new ImVec4();
        ImGui.getStyleColorVec4(ImGuiCol.MenuBarBg, themeColor);

        ImGui.pushStyleColor(ImGuiCol.FrameBg, ImColor.rgba(themeColor.x, themeColor.y, themeColor.z, 1f));
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 8);
        ImGui.beginChildFrame(500, 200, ImGui.getContentRegionAvailY() - 20);
        ImGui.popStyleColor();

        for (int i = 0; i < propertiesList.size(); i++) {
            propertiesList.get(i).RenderOptions();
        }

        if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 30)) {
            graph.AddProperty("New Property");
            UpdateProperties();
        }

        ImGui.endChildFrame();
        ImGui.popStyleVar();
    }

    public static void UpdateProperties() {
        propertiesList.clear();
        propertiesList.addAll(graph.GetProperties());
    }

    private static final HashMap<Integer, Integer> ColorTheme = new HashMap<>();
    public static void SetTheme() {
        ColorTheme.clear();

        ColorTheme.put(ImNodesColorStyle.GridBackground, ImGui.getColorU32(ImGuiCol.TitleBg));
        ColorTheme.put(ImNodesColorStyle.GridLine, ImGui.getColorU32(ImGuiCol.TitleBgActive));
        ColorTheme.put(ImNodesColorStyle.Link, ImColor.rgb("#43a257"));
        ColorTheme.put(ImNodesColorStyle.LinkHovered, ImColor.rgb("#60e17c"));
        ColorTheme.put(ImNodesColorStyle.LinkSelected, ImColor.rgb("#60e17c"));
        ColorTheme.put(ImNodesColorStyle.Pin, ImColor.rgb("#43a257"));
        ColorTheme.put(ImNodesColorStyle.PinHovered, ImColor.rgb("#60e17c"));
    }

    private static final HashMap<String, Method> methods = new HashMap<>();
    private static Method GetNode(String name) {
        if (methods.containsKey(name)) return methods.get(name);

        try {
            Method res = Nodes.class.getMethod(name);
            methods.put(name, res);
            return res;
        } catch (Exception e) {
            Console.Error(e);
            return null;
        }
    }

    private static void DestroyLink(int id) {
        Link link = Link.GetLinks(id);
        NodeOutput output = (NodeOutput) NodeIO.GetIO(link.startIo);
        NodeInput input = (NodeInput) NodeIO.GetIO(link.endIo);

        input.link = null;
        output.links.remove(link);
        output.SetBaseObject();

        Link.DestroyLink(id);
    }

    private static float InverseLerp(float imin, float imax, float omin, float omax, float v) {
        float t = (v - imin) / (imax - imin);
        float lerp = (1f - t) * omin + omax * t;

        return lerp;
    }


}
