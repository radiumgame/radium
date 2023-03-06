package Radium.Engine.Scripting.Node.Types;

import Radium.Engine.Math.Vector.Vector2;

public class Vector2Type extends NodeIoType {

    public Vector2Type() {
        super("Vector2", new Vector2());
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("Vector2");
    }

}
