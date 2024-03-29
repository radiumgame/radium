package Radium.Editor;

import Radium.Engine.Graphics.Texture;

import java.util.HashMap;

public class Icons {

    private static HashMap<String, Integer> icons = new HashMap<>();

    protected Icons() {}

    public static void Initialize() {
        icons.put("animation", Load("EngineAssets/Editor/Explorer/animation.png"));
        icons.put("audio", Load("EngineAssets/Editor/Explorer/audio.png"));
        icons.put("prefab", Load("EngineAssets/Editor/gameobject.png"));
        icons.put("node_event", Load("EngineAssets/Editor/NodeEditor/event.png"));
        icons.put("node_graph", Load("EngineAssets/Editor/Icons/node_scripting.png"));
        icons.put("search", Load("EngineAssets/Editor/search.png"));
    }

    public static int GetIcon(String name) {
        return icons.getOrDefault(name, 0);
    }

    private static int Load(String path) {
        return new Texture(path, true).GetTextureID();
    }

}
