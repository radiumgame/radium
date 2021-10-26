package Engine.Util;

import Editor.Console;

public class NonInstantiatable {

    public NonInstantiatable() {
        Console.Error("Cannot instantiate " + getClass().getSimpleName() + " class");
    }

}
