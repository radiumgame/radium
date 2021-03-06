package Radium.Engine.Objects;

import Radium.Engine.Graphics.Texture;
import Radium.Engine.SceneManagement.Scene;
import Radium.Editor.Console;
import Radium.Engine.Application;
import Radium.Engine.Component;
import Radium.Engine.Math.Transform;
import Radium.Engine.SceneManagement.SceneManager;
import Radium.Engine.Serialization.Serializer;
import Radium.Engine.Serialization.TypeAdapters.ClassTypeAdapter;
import Radium.Engine.Serialization.TypeAdapters.ComponentTypeAdapter;
import Radium.Engine.Serialization.TypeAdapters.GameObjectTypeAdapter;
import Radium.Engine.Serialization.TypeAdapters.TextureTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Objects that can be created and contain components
 */
public class GameObject implements Cloneable {

    /**
     * The game object's name
     */
    public String name = "New Game Object";
    /**
     * Transform of the game object
     */
    public Transform transform;

    public String id;

    private transient GameObject parent;
    private String parentID;

    private List<Component> components = new ArrayList<>();
    private transient List<GameObject> children = new ArrayList<>();

    /**
     * Create empty game object and add to scene
     */
    public GameObject() {
        transform = new Transform();
        SceneManager.GetCurrentScene().gameObjectsInScene.add(this);

        id = UUID.randomUUID().toString();
    }

    /**
     * Create empty game object, but only added to scene if instantiate is true
     * @param instantiate Instantiate game object to scene
     */
    public GameObject(boolean instantiate) {
        transform = new Transform();
        if (instantiate) {
            SceneManager.GetCurrentScene().gameObjectsInScene.add(this);
        }

        id = UUID.randomUUID().toString();
    }

    /**
     * When editor plays, it saves a clone of itself
     */
    public void OnPlay() {

    }

    /**
     * Resets the game object to its clone create in OnPlay()
     */
    public void OnStop() {

    }

    /**
     * Destroys the game object and removes it from the scene
     */
    public void Destroy() {
        Destroy(true);
    }

    /**
     * Destroys the game object, but only removed from the scene if clear is true
     * @param clear Remove from scene
     */
    public void Destroy(boolean clear) {
        for (int i = 0; i < components.size(); i++) {
            RemoveComponent(components.get(i).getClass());
        }
        components.clear();

        for (GameObject child : children) {
            child.Destroy(true);
        }

        if (clear) {
            SceneManager.GetCurrentScene().gameObjectsInScene.remove(this);
        }
    }

    /**
     * Returns component if available
     * @param componentClass Component to get
     * @return Component if available
     */
    public <T extends Component> T GetComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (Exception e) {
                    Console.Error(e);
                }
            }
        }
        return null;
    }

    /**
     * Adds new component to game object
     * @param component New component
     * @return The created component
     */
    public Component AddComponent(Component component) {
        components.add(component);

        component.gameObject = this;
        component.OnAdd();

        if (Application.Playing && !Scene.RuntimeSerialization) component.Start();

        return component;
    }

    /**
     * Removes component from game obejct
     * @param componentClass Component class to remove
     */
    public <T extends Component> void RemoveComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                c.OnRemove();

                components.remove(i);
            }
        }
    }

    /**
     * @return All components on game object
     */
    public List<Component> GetComponents() {
        return components;
    }

    /**
     * Returns if component is available
     * @param component Component to check
     * @return Component is available
     */
    public boolean ContainsComponent(Class<? extends Component> component) {
        return GetComponent(component) != null;
    }

    /**
     * Returns the game object parent if it has one
     * @return Parent object
     */
    public GameObject GetParent() {
        return parent;
    }

    /**
     * Sets the parent to a new game object
     * @param newParent New parent
     */
    public void SetParent(GameObject newParent) {
        if (newParent == this || newParent == parent) {
            return;
        }

        if (newParent == null) {
            return;
        }

        if (parent != null) {
            parent.RemoveChild(this);
        }

        parent = newParent;
        parentID = parent.id;
        parent.AddChild(this);
    }

    /**
     * Removes the parent and sets it to null
     */
    public void RemoveParent() {
        if (parent != null) {
            parent.RemoveChild(this);
        }

        parentID = null;
        parent = null;
    }

    /**
     * Returns children
     * @return Game object children
     */
    public List<GameObject> GetChildren() {
        return children;
    }

    protected void AddChild(GameObject child) {
        children.add(child);
    }

    protected void RemoveChild(GameObject child) { children.remove(child); }

    /**
     * Clones the game objects transform, name, and components
     * @return New cloned game object
     */
    public GameObject Clone()
    {
        Gson gson = Serializer.Serializer;
        return gson.fromJson(gson.toJson(this), GameObject.class);
    }

    public static GameObject Find(String id) {
        List<GameObject> gameObjects = SceneManager.GetCurrentScene().gameObjectsInScene;
        for (GameObject go : gameObjects) {
            if (go.id.equals(id)) {
                return go;
            }
        }

        return null;
    }

}
