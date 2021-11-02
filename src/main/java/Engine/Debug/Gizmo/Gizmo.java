package Engine.Debug.Gizmo;

public abstract class Gizmo {

    public abstract void Update();
    public abstract void OnDestroy();

    public void Destroy() {
        OnDestroy();
        GizmoManager.gizmos.remove(this);
    }

}
