package Radium.Util;

/**
 * Utility for dealing with enums
 */
public class EnumUtility {

    /**
     * Get values of an enum
     * @param enumType Target enum
     * @return String[] of enum value names
     */
    public static <T extends Enum<T>> String[] GetValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T t : enumType.getEnumConstants()) {
            enumValues[i] = t.name();
            i++;
        }

        return enumValues;
    }

    /**
     * Gets index of enum value if name equals any enum value
     * @param str Value name
     * @param array Enum values
     */
    public static int GetIndex(String str, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (str.equals(array[i])) return i;
        }

        return -1;
    }

}
