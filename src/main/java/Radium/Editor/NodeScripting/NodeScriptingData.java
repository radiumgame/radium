package Radium.Editor.NodeScripting;

import java.util.*;

public class NodeScriptingData {

    public static Map<String, List<String>> allNodes = new TreeMap<>();
    public static List<String> allNodeNames = new ArrayList<>();
    public static List<String> filtered = new ArrayList<>();
    public static boolean Filtering = false;

    protected NodeScriptingData() {}

    public static void Initialize() {
        allNodes.put("Console", List.of("Log", "Warn", "Error"));
        allNodes.put("Data Types", List.of("Int", "Float", "String", "Vector2", "Vector3"));
        allNodes.put("Math", List.of("Add", "Subtract", "Multiply", "Divide"));
        allNodes.put("Transform", List.of("Translate", "Rotate", "Scale", "SetPosition", "SetRotation", "SetScale"));

        allNodes.forEach((submenu, items) -> items.forEach(item -> allNodeNames.add(item)));
    }

    public static void Filter(String search) {
        if (search.isBlank()) {
            Filtering = false;
            filtered.clear();
            return;
        }

        filtered.clear();
        for (String node : allNodeNames) {
            if (node.toLowerCase().contains(search.toLowerCase())) filtered.add(node);
        }

        Filtering = true;
    }

}
