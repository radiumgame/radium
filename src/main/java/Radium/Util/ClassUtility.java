package Radium.Util;

import Radium.Color;
import RadiumEditor.Console;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ClassUtility {

    protected ClassUtility() {}

    public static Object Clone(Object obj) {
        try {
            Object instance = obj.getClass().getDeclaredConstructor().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                field.set(instance, CloneField(field, obj));
            }

            return instance;
        } catch (Exception e) {
            Console.Error(e);
            return null;
        }
    }

    public static void CopyFields(Object from, Object to) {
        if (!from.getClass().isAssignableFrom(to.getClass())) {
            Console.Error("Cannot copy fields from " + from.getClass().getName() + " to " + to.getClass().getName());
            return;
        }

        for (Field field : from.getClass().getDeclaredFields()) {
            if (Modifier.isPrivate(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            try {
                field.set(to, CloneField(field, from));
            } catch (Exception e) {
                Console.Error(e);
            }
        }
    }

    private static Object CloneField(Field field, Object obj) throws Exception {
        Class type = field.getType();
        Object original = field.get(obj);
        Object set;
        if (type == Integer.class) {
            set = new Integer((int)original);
        } else if (type == Float.class) {
            set = new Float((float)original);
        } else if (type == Double.class) {
            set = new Double((double)original);
        } else if (type == Boolean.class) {
            set = new Boolean((boolean)original);
        } else if (type == String.class) {
            set = new String((String)original);
        } else if (type == Color.class) {
            Color o = (Color)original;
            set = new Color(o.r, o.g, o.b, o.a);
        } else {
            set = original;
        }

        return set;
    }

}
