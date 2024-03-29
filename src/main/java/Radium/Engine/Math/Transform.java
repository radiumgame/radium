package Radium.Engine.Math;

import Radium.Engine.Math.Vector.Vector3;
import Radium.Engine.Objects.GameObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joml.Vector3f;

/**
 * Transform system for objects
 */
public class Transform {

    @Deprecated
    public Vector3 position = Vector3.Zero();
    @Deprecated
    public Vector3 rotation = Vector3.Zero();
    @Deprecated
    public Vector3 scale = Vector3.One();

    /**
     * Object position
     */
    public Vector3 localPosition = Vector3.Zero();
    /**
     * Object rotation
     */
    public Vector3 localRotation = Vector3.Zero();
    /**
     * Object scale
     */
    public Vector3 localScale = Vector3.One();

    @JsonIgnore
    private Vector3 worldPosition = position, worldRotation = rotation, worldScale = scale;

    /**
     * Updates the world position
     * @param obj Updates world position of game object
     */
    public void Update(GameObject obj) {
        if (obj.GetParent() != null) {
            Transform parentTransform = obj.GetParent().transform;

            worldPosition = Vector3.Add(parentTransform.WorldPosition(), localPosition);
            worldRotation = Vector3.Add(parentTransform.WorldRotation(), localRotation);
            worldScale = Vector3.Multiply(parentTransform.WorldScale(), localScale);
        } else {
            worldPosition = localPosition;
            worldRotation = localRotation;
            worldScale = localScale;
        }
    }

    /**
     * World position based off parent position
     * @return World position
     */
    public Vector3 WorldPosition() {
        return worldPosition;
    }

    /**
     * World rotation based off parent rotation
     * @return World rotation
     */
    public Vector3 WorldRotation() {
        return worldRotation;
    }

    /**
     * World scale based off parent scale
     * @return World scale
     */
    public Vector3 WorldScale() {
        return worldScale;
    }

    /**
     * Calculates transform forward vector
     * @return Forward vector
     */
    public Vector3 Forward() {
        float x = Mathf.Sine(Mathf.Radians(WorldRotation().y)) * Mathf.Cosine(Mathf.Radians(WorldRotation().x));
        float y = Mathf.Sine(Mathf.Radians(-WorldRotation().x));
        float z = Mathf.Cosine(Mathf.Radians(WorldRotation().x)) * Mathf.Cosine(Mathf.Radians(WorldRotation().y));

        return new Vector3(x, y, -z);
    }

    /**
     * Reverse forward vector
     * @return Back vector
     */
    public Vector3 Back() {
        Vector3 forward = Forward();

        return new Vector3(-forward.x, -forward.y, -forward.z);
    }

    /**
     * Calculates transform right vector
     * @return Right vector
     */
    public Vector3 Right() {
        Vector3 forward = Forward();
        Vector3f forwardf = new Vector3f(forward.x, forward.y, forward.z);
        Vector3f cross = forwardf.cross(new Vector3f(0, 1, 0));
        Vector3 radiumCross = new Vector3(cross.x, cross.y, cross.z);

        return Vector3.Normalized(radiumCross);
    }

    /**
     * Calculates transform up vector
     * @return Up vector
     */
    public Vector3 Up() {
        Vector3 right = Right();
        Vector3 forward = Forward();

        Vector3f rightf = new Vector3f(right.x, right.y, right.z);
        Vector3f forwardf = new Vector3f(forward.x, forward.y, forward.z);

        Vector3f cross = rightf.cross(forwardf);
        Vector3 radiumCross = new Vector3(cross.x, cross.y, cross.z);

        return Vector3.Normalized(radiumCross);
    }

    /**
     * Calculates transform forward vector
     * @return Forward vector
     */
    public Vector3 EditorForward() {
        float x = Mathf.Sine(Mathf.Radians(rotation.y)) * Mathf.Cosine(Mathf.Radians(rotation.x));
        float y = Mathf.Sine(Mathf.Radians(-rotation.x));
        float z = Mathf.Cosine(Mathf.Radians(rotation.x)) * Mathf.Cosine(Mathf.Radians(rotation.y));

        return new Vector3(x, y, -z);
    }

    /**
     * Calculates transform right vector
     * @return Right vector
     */
    public Vector3 EditorRight() {
        Vector3 forward = EditorForward();
        Vector3f forwardf = new Vector3f(forward.x, forward.y, forward.z);
        Vector3f cross = forwardf.cross(new Vector3f(0, 1, 0));
        Vector3 radiumCross = new Vector3(cross.x, cross.y, cross.z);

        return Vector3.Normalized(radiumCross);
    }

    /**
     * Calculates transform up vector
     * @return Up vector
     */
    public Vector3 EditorUp() {
        Vector3 right = EditorRight();
        Vector3 forward = EditorForward();

        Vector3f rightf = new Vector3f(right.x, right.y, right.z);
        Vector3f forwardf = new Vector3f(forward.x, forward.y, forward.z);

        Vector3f cross = rightf.cross(forwardf);
        Vector3 radiumCross = new Vector3(cross.x, cross.y, cross.z);

        return Vector3.Normalized(radiumCross);
    }

    public static Transform Clone(Transform transform) {
        Transform t = new Transform();
        t.localPosition = transform.localPosition;
        t.localRotation = transform.localRotation;
        t.localScale = transform.localScale;
        t.position = transform.position;
        t.rotation = transform.rotation;
        t.scale = transform.scale;

        return t;
    }

    public void SetPositionFromWorld(Vector3 worldPos) {
        Vector3 difference = Vector3.Subtract(worldPos, worldPosition);
        localPosition.Add(difference);
    }

    public Transform Clone() {
        Transform t = new Transform();
        t.localPosition = localPosition;
        t.localRotation = localRotation;
        t.localScale = localScale;
        t.position = position;
        t.rotation = rotation;
        t.scale = scale;
        t.worldPosition = worldPosition;
        t.worldRotation = worldRotation;
        t.worldScale = worldScale;

        return t;
    }

    @Override
    public boolean equals(Object other) {
        Transform o = (Transform) other;
        if (o == null) return false;
        return WorldPosition().equals(o.WorldPosition()) && WorldRotation().equals(o.WorldRotation()) && WorldScale().equals(o.WorldScale());
    }

}
