package Radium.Engine.Color;

import Radium.Engine.Math.Mathf;
import Radium.Engine.Time;
import imgui.ImColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Gradient {

    public List<Key> keys = new ArrayList<>();
    public Key selectedKey = null;

    public Gradient() {
        keys.add(new Key(0, new Color(0, 0, 0)));
        keys.add(new Key(0.5f, new Color(1.0f, 0, 0)));
        keys.add(new Key(1, new Color(1.0f, 1.0f, 1.0f)));
    }

    public int First() {
        Color col = keys.get(0).color;
        return ImColor.floatToColor(col.r, col.g, col.b, col.a);
    }

    public int Last() {
        Color col = keys.get(keys.size() - 1).color;
        return ImColor.floatToColor(col.r, col.g, col.b, col.a);
    }

    public Key AddKey(float position, Color color) {
        Key k = new Key(position, color);
        keys.add(k);

        Collections.sort(keys, (a, b) -> {
            if (a.position < b.position) return -1;
            if (a.position > b.position) return 1;
            return 0;
        });

        return k;
    }

    public boolean RemoveKey(Key key) {
        if (keys.size() - 1 == 0) return false;

        keys.remove(key);
        return true;
    }

    public void Sort() {
        Collections.sort(keys, (a, b) -> {
            if (a.position < b.position) return -1;
            if (a.position > b.position) return 1;
            return 0;
        });
    }

    public Color GetColor(float value) {
        if (keys.get(0).position > value) return keys.get(0).color;
        if (keys.get(keys.size() - 1).position < value) return keys.get(keys.size() - 1).color;

        for (int i = 0; i < keys.size(); i++) {
            Key key = keys.get(i);

            if (value >= key.position && value <= keys.get(i + 1).position) {
                float t = (value - key.position) / (keys.get(i + 1).position - key.position);
                return Color.Lerp(key.color, keys.get(i + 1).color, t);
            }
        }

        return new Color(0, 0, 0, 1.0f);
    }

    public Color Ease() {
        float time = Time.GetTime();
        float dec = time - Mathf.Floor(time);
        float sin = Mathf.Sine(dec);

        return GetColor(sin);
    }

    public class Key {

        public float position;
        public Color color;

        public Key(float position, Color color) {
            this.position = position;
            this.color = color;
        }

    }

}
