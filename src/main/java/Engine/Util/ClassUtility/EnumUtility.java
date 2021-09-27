package Engine.Util.ClassUtility;

public class EnumUtility {

    public static <T extends Enum<T>> String[] GetValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T t : enumType.getEnumConstants()) {
            enumValues[i] = t.name();
            i++;
        }

        return enumValues;
    }

    public static int GetIndex(String str, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (str.equals(array[i])) return i;
        }

        return -1;
    }

}
