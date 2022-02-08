package Radium.Util;

import RadiumEditor.Console;
import at.favre.lib.bytes.Bytes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for converting types into byte[]
 */
public class ByteUtility {

    protected ByteUtility() {}

    /**
     * Converts an int to a byte[4]
     * @return int -> byte[]
     */
    public static byte[] GetBytes(int value) {
        return FromBytes(Bytes.from(value).toBoxedArray());
    }

    /**
     * Converts a float to a byte[4]
     * @return float -> byte[]
     */
    public static byte[] GetBytes(float value) {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    /**
     * Converts a boolean to a byte[1]
     * @return boolean -> byte[]
     */
    public static byte[] GetBytes(boolean value) {
        byte[] bytes = new byte[] { (byte)(value ? 1 : 0) };
        return bytes;
    }

    /**
     * Converts a string to a byte[]
     * @return string -> byte[]
     */
    public static byte[] GetBytes(String value) {
        return value.getBytes();
    }

    /**
     * Converts byte[] into an int
     * @param value Data
     * @param startIndex Where to start reading data
     * @return byte[] -> int
     */
    public static int ToInt(byte[] value, int startIndex) {
        return Bytes.from(ToByteList(value).subList(startIndex, startIndex + 4)).toInt();
    }

    /**
     * Converts byte[] into a float
     * @param value Data
     * @param startIndex Where to start reading data
     * @return byte[] -> float
     */
    public static float ToFloat(byte[] value, int startIndex) {
        return ByteBuffer.wrap(value, startIndex, 4).getFloat();
    }

    /**
     * Converts byte[] into a boolean
     * @param value Data
     * @param startIndex Where to start reading data
     * @return byte[] -> boolean
     */
    public static boolean ToBoolean(byte[] value, int startIndex) {
        byte booleanValue = value[startIndex];

        return (booleanValue == 1) ? true : false;
    }

    /**
     * Converts byte[] into a string
     * @param value Data
     * @param startIndex Where to start reading data
     * @param length Length of string
     * @return byte[] -> string
     */
    public static String ToString(byte[] value, int startIndex, int length) {
        Console.Log(startIndex);
        Console.Log(length);
        byte[] stringBytes = ToByteArray(ToByteList(value).subList(startIndex, startIndex + length));

        String stringValue = new String(stringBytes, StandardCharsets.UTF_8);
        return stringValue;
    }

    private static List<Byte> ToByteList(byte[] data) {
        List<Byte> returnBytes = new ArrayList<>();
        for (byte b : data) {
            returnBytes.add(b);
        }

        return returnBytes;
    }

    private static byte[] ToByteArray(List<Byte> bytes) {
        byte[] returnBytes = new byte[bytes.size()];

        for (int i = 0; i < returnBytes.length; i++) {
            returnBytes[i] = bytes.get(i);
        }

        return returnBytes;
    }

    private static byte[] FromBytes(Byte[] bytes) {
        byte[] returnBytes = new byte[bytes.length];

        for (int i = 0; i < returnBytes.length; i++) {
            returnBytes[i] = bytes[i];
        }

        return returnBytes;
    }

}
