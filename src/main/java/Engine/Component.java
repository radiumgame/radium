package Engine;

import Engine.Objects.GameObject;

public abstract class Component {

    public GameObject gameObject;

    public abstract void Start();
    public abstract void Update();

}
