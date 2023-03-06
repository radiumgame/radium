package Radium.Engine.Scripting.Node.Types;

public class GameObjectType extends NodeIoType {

    public GameObjectType() {
        super("GameObject", "");
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("GameObject");
    }

}
