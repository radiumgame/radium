package Engine.Util;

import Editor.Console;

public class NonInstantiatable {

    public NonInstantiatable() {
        Console.Error("You should not instantiate " + getClass().getSimpleName() + " class");
    }

}
