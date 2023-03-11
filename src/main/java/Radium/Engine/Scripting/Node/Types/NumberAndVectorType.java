package Radium.Engine.Scripting.Node.Types;

public class NumberAndVectorType extends NodeIoType {

    public NumberAndVectorType() {
        super("NumberAndVector", 0);
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("Int") || other.name.equals("Float") || other.name.equals("Vector2") || other.name.equals("Vector3");
    }

}
