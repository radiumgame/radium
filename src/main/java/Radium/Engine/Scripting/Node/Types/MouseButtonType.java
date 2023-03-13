package Radium.Engine.Scripting.Node.Types;

import Radium.Engine.Input.MouseButton;

public class MouseButtonType extends NodeIoType {

    public MouseButtonType() {
        super("Mouse Button", MouseButton.Left);
    }

    @Override
    public boolean CanLink(NodeIoType other) {
        return other.name.equals("Mouse Button");
    }

}
