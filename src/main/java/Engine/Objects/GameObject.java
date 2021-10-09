package Engine.Objects;

import Editor.Console;
import Engine.Component;
import Engine.Components.Graphics.MeshRenderer;
import Engine.Math.Transform;
import Engine.SceneManagement.SceneManager;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    public String name = "New Game Object";
    public Transform transform;

    private List<Component> components = new ArrayList<Component>();

    public GameObject() {
        transform = new Transform();
        SceneManager.GetCurrentScene().gameObjectsInScene.add(this);
    }

    public void Destroy() {
        for (int i = 0; i < components.size(); i++) {
            RemoveComponent(components.get(i).getClass());
        }

        components.clear();
    }

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

    public Component AddComponent(Component component) {
        components.add(component);

        component.gameObject = this;

        return component;
    }

    public <T extends Component> void RemoveComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.get(i).OnRemove();
                components.remove(i);
                return;
            }
        }
    }

    public List<Component> GetComponents() {
        return components;
    }

    public boolean ContainsComponent(Class component) {
        return GetComponent(component) != null;
    }
}
