package Radium.Engine.Scripting.Node.Types;

import Radium.Engine.Input.Keys;

public class KeyType extends NodeIoType {

    public KeyType() {
        super("Key", Keys.None);
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("Key");
    }

}
