package Radium.Engine.Scripting.Node.Types;

public class StringType extends NodeIoType {

    public StringType() {
        super("String", "");
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("String");
    }

}
