package Radium.Engine.Scripting.Node.Types;

public class ObjectType extends NodeIoType {

    public ObjectType() {
        super("Object", new NullType("Object"));
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return true;
    }

}
