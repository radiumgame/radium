package Radium.Engine.Scripting.Node.Types;

public class BooleanType extends NodeIoType {

    public BooleanType() {
        super("Boolean", false);
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("Boolean");
    }

}
