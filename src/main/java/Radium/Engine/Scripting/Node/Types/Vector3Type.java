package Radium.Engine.Scripting.Node.Types;

import Radium.Engine.Math.Vector.Vector3;

public class Vector3Type extends NodeIoType {

    public Vector3Type() {
        super("Vector3", new Vector3());
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("Vector3");
    }

}
