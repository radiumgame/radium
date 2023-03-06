package Radium.Engine.Scripting.Node.Types;

public class FloatType extends NodeIoType {

    public FloatType() {
        super("Float", 0.0f);
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        boolean isInt = other.name.equals("Int");
        boolean isFloat = other.name.equals("Float");

        return isInt || isFloat;
    }

}
