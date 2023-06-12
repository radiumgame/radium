package Radium.Engine.Scripting.Node.Types;

public class IntType extends NodeIoType {

    public IntType() {
        super("Int", 0);
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("Int");
    }

}
