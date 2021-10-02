package Engine.Util;

public class NonInstantiatable {

    public NonInstantiatable() {
        throw new UnsupportedOperationException("Cannot instantiate " + getClass().getSimpleName() + " class");
    }

}
