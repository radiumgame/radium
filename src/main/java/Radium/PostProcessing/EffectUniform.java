package Radium.PostProcessing;

import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Util.IDGenerator;

import java.util.UUID;

public class EffectUniform {

    private static IDGenerator generator = new IDGenerator(IDGenerator.IDGenType.Random);

    public String name = "col";
    public Class type = Integer.class;
    public Object value;

    public transient int selectedType = 0;

    public transient int id = generator.NewID();

    public void AssignDefaultValue() {
        if (type == Integer.class) {
            value = 0;
        } else if (type == Float.class) {
            value = 0f;
        } else if (type == Boolean.class) {
            value = false;
        } else if (type == Vector2.class) {
            value = Vector3.Zero();
        } else if (type == Vector3.class) {
            value = Vector3.Zero();
        } else {
            value = 0;
        }
    }

    public void AssignType() {
        if (type == Integer.class) {
            selectedType = UniformType.Integer.ordinal();
        } else if (type == Float.class) {
            selectedType = UniformType.Float.ordinal();
        } else if (type == Boolean.class) {
            selectedType = UniformType.Boolean.ordinal();
        } else if (type == Vector2.class) {
            selectedType = UniformType.Vector2.ordinal();
        } else if (type == Vector3.class) {
            selectedType = UniformType.Vector3.ordinal();
        } else {
            value = 0;
        }
    }

}
