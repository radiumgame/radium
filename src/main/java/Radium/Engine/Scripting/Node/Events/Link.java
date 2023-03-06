package Radium.Engine.Scripting.Node.Events;

import Radium.Engine.Scripting.Node.IO.NodeIO;
import Radium.Engine.Scripting.Node.NodeGraph;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.HashMap;

public class Link {

    private static int NextLinkID = 0;
    private static final HashMap<Integer, Link> links = new HashMap<>();

    @JsonIgnore
    public int id;
    public int startNode;
    public int endNode;
    public int startIo;
    public int endIo;

    public String startName;
    public String endName;

    public Link(int start, int end, int startIo, int endIo, NodeGraph graph) {
        id = NextLinkID;
        startNode = start;
        endNode = end;
        this.startIo = startIo;
        this.endIo = endIo;

        startName = NodeIO.GetIO(startIo).name;
        endName = NodeIO.GetIO(endIo).name;

        NextLinkID++;
        graph.links.add(this);
        links.put(id, this);
    }

    public static Link GetLinks(int id) {
        return links.get(id);
    }

    public static void DestroyLink(int id) {
        links.remove(id);
    }

    public static Collection<Link> GetAllLinks() {
        return links.values();
    }

}
