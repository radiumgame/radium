package RadiumEditor.Debug.Gizmo;

/**
 * An indicator that can render meshes and icons
 */
public abstract class Gizmo {

    /**
     * Called every frame in edit mode
     */
    public abstract void Update();

    /**
     * Destroys mesh and components
     */
    public abstract void OnDestroy();

    /**
     * Destroys mesh and removes from {@link GizmoManager gizmo manager}
     */
    public void Destroy() {
        OnDestroy();
        GizmoManager.gizmos.remove(this);
    }

}
