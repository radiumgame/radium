package Engine.SceneManagement;

import Engine.Component;
import Engine.Objects.GameObject;
import java.util.ArrayList;
import java.util.List;

public class Scene {

    public List<GameObject> gameObjectsInScene = new ArrayList<>();

    public void Update() {
        for (int i = 0; i < gameObjectsInScene.size(); i++) {
            GameObject go = gameObjectsInScene.get(i);

            for (Component comp : go.GetComponents()) {
                comp.Update();
            }
        }
    }

    public boolean ContainsComponent(Class component) {
        boolean result = false;

        for (GameObject go : gameObjectsInScene) {
            if (go.ContainsComponent(component)) {
                result = true;
            }
        }

        return result;
    }
}
