package Radium.Networking;

import RadiumEditor.Console;
import Radium.Color.Color;
import Radium.Math.Transform;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import Radium.Util.ByteUtility;

import java.util.List;
import java.util.ArrayList;

/**
 * Converts objects to bytes and packages them into array
 */
public class Packet {

    private List<Byte> buffer;
    private byte[] readableBuffer;
    private int readPos;

    /**
     * Create empty packet with no ID
     */
    public Packet() {
        buffer = new ArrayList<>();
        readPos = 0;
    }

    /**
     * Create packet with an ID
     * @param id Packet ID
     */
    public Packet(int id) {
        buffer = new ArrayList<>();
        readPos = 0;

        Write(id);
    }

    /**
     * Create packet with data predefined
     * @param data Bytes to set
     */
    public Packet(byte[] data) {
        buffer = new ArrayList<>();
        readPos = 0;

        SetBytes(data);
    }

    /**
     * Sets the packet bytes
     * @param bytes Data to set
     */
    public void SetBytes(byte[] bytes) {
        Write(bytes);
        readableBuffer = ToByteArray(buffer.toArray());
    }

    /**
     * Resets packet data
     */
    public void Reset() {
        buffer.clear();
        readableBuffer = null;
        readPos = 0;
    }

    public void Reset(boolean shouldReset) {
        if (shouldReset) {
            Reset();
        } else {
            readPos -= 4;
        }
    }

    /**
     * Writes byte to the packet
     * @param value Data to write
     */
    public void Write(byte value) {
        buffer.add(value);
    }

    /**
     * Writes byte[] to the packet
     * @param value Data to write
     */
    public void Write(byte[] value) {
        AddRange(value);
    }

    /**
     * Writes int to the packet
     * @param value Data to write
     */
    public void Write(int value) {
        byte[] bytes = ByteUtility.GetBytes(value);
        AddRange(bytes);
    }

    /**
     * Writes float to the packet
     * @param value Data to write
     */
    public void Write(float value) {
        byte[] bytes = ByteUtility.GetBytes(value);
        AddRange(bytes);
    }

    /**
     * Writes boolean to the packet
     * @param value Data to write
     */
    public void Write(boolean value) {
        byte[] bytes = ByteUtility.GetBytes(value);
        AddRange(bytes);
    }

    /**
     * Writes string to the packet
     * @param value Data to write
     */
    public void Write(String value) {
        Write(value.length());

        byte[] bytes = ByteUtility.GetBytes(value);
        AddRange(bytes);
    }

    /**
     * Writes Vector2 to the packet
     * @param value Data to write
     */
    public void Write(Vector2 value) {
        Write(value.x);
        Write(value.y);
    }

    /**
     * Writes Vector3 to the packet
     * @param value Data to write
     */
    public void Write(Vector3 value) {
        Write(value.x);
        Write(value.y);
        Write(value.z);
    }

    /**
     * Writes transform to the packet
     * @param value Data to write
     */
    public void Write(Transform value) {
        Write(value.WorldPosition().x);
        Write(value.WorldPosition().y);
        Write(value.WorldPosition().z);

        Write(value.WorldRotation().x);
        Write(value.WorldRotation().y);
        Write(value.WorldRotation().z);

        Write(value.WorldScale().x);
        Write(value.WorldScale().y);
        Write(value.WorldScale().z);
    }

    /**
     * Writes color to the packet
     * @param value Data to write
     */
    public void Write(Color value) {
        Write(value.ToVector3());
    }

    /**
     * Writes int[] to the packet
     * @param value Data to write
     */
    public void Write(int[] value) {
        Write(value.length);
        for (int integer : value) {
            Write(integer);
        }
    }

    /**
     * Writes float[] to the packet
     * @param value Data to write
     */
    public void Write(float[] value) {
        Write(value.length);
        for (float dec : value) {
            Write(dec);
        }
    }

    /**
     * Reads byte from current read position
     * @return
     */
    public byte ReadByte() {
        if (buffer.size() > readPos) {
            byte value = readableBuffer[readPos];

            readPos++;
            return value;
        } else {
            Console.Error("Couldn't read type byte from packet");
            return -1;
        }
    }

    /**
     * Reads byte[] from current read position
     * @return
     */
    public byte[] ReadBytes(int length) {
        if (buffer.size() > readPos) {
            byte[] value = ToByteArray(buffer.subList(readPos, readPos + length).toArray());
            readPos += length;

            return value;
        } else {
            Console.Error("Couldn't read type byte[] from packet");
            return null;
        }
    }

    /**
     * Reads int from current read position
     * @return
     */
    public int ReadInt() {
        if (buffer.size() > readPos) {
            int value = ByteUtility.ToInt(readableBuffer, readPos);
            readPos += 4;

            return value;
        } else {
            Console.Error("Couldn't read type int from packet");
            return -1;
        }
    }

    /**
     * Reads float from current read position
     * @return
     */
    public float ReadFloat() {
        if (buffer.size() > readPos) {
            float value = ByteUtility.ToFloat(readableBuffer, readPos);
            readPos += 4;

            return value;
        } else {
            Console.Error("Couldn't read type float from packet");
            return -1;
        }
    }

    /**
     * Reads boolean from current read position
     * @return
     */
    public boolean ReadBoolean() {
        if (buffer.size() > readPos) {
            boolean value = ByteUtility.ToBoolean(readableBuffer, readPos);
            readPos += 1;

            return value;
        } else {
            Console.Error("Couldn't read type boolean from packet");
            return false;
        }
    }

    /**
     * Reads string from current read position
     * @return
     */
    public String ReadString() {
        if (buffer.size() > readPos) {
            int length = ReadInt();
            String value = ByteUtility.ToString(readableBuffer, readPos, length);

            if (value.length() > 0) {
                readPos += length;
            }

            return value;
        } else {
            Console.Error("Couldn't read type String from packet");
            return null;
        }
    }

    /**
     * Reads Vector2 from current read position
     * @return
     */
    public Vector2 ReadVector2() {
        if (buffer.size() > readPos) {
            float x = ReadFloat();
            float y = ReadFloat();

            return new Vector2(x, y);
        } else {
            Console.Error("Couldn't read type Vector2 from packet");
            return null;
        }
    }

    /**
     * Reads Vector3 from current read position
     * @return
     */
    public Vector3 ReadVector3() {
        if (buffer.size() > readPos) {
            float x = ReadFloat();
            float y = ReadFloat();
            float z = ReadFloat();

            return new Vector3(x, y, z);
        } else {
            Console.Error("Couldn't read type Vector3 from packet");
            return null;
        }
    }

    /**
     * Reads transform from current read position
     * @return
     */
    public Transform ReadTransform() {
        if (buffer.size() > readPos) {
            float xPosition = ReadFloat();
            float yPosition = ReadFloat();
            float zPosition = ReadFloat();
            Vector3 position = new Vector3(xPosition, yPosition, zPosition);

            float xRotation = ReadFloat();
            float yRotation = ReadFloat();
            float zRotation = ReadFloat();
            Vector3 rotation = new Vector3(xRotation, yRotation, zRotation);

            float xScale = ReadFloat();
            float yScale = ReadFloat();
            float zScale = ReadFloat();
            Vector3 scale = new Vector3(xScale, yScale, zScale);

            Transform transform = new Transform();
            transform.position = position;
            transform.rotation = rotation;
            transform.scale = scale;

            return transform;
        } else {
            Console.Error("Couldn't read type Vector3 from packet");
            return null;
        }
    }

    /**
     * Reads color from current read position
     * @return
     */
    public Color ReadColor() {
        if (buffer.size() > readPos) {
            return Color.FromVector3(ReadVector3());
        } else {
            Console.Error("Couldn't read type Color from packet");
            return null;
        }
    }

    /**
     * Reads int[] from current read position
     * @return
     */
    public int[] ReadIntArray() {
        if (buffer.size() > readPos) {
            int length = ReadInt();
            int[] values = new int[length];

            for (int i = 0; i < values.length; i++) {
                values[i] = ReadInt();
            }

            return values;
        } else {
            Console.Error("Couldn't read type float[] from packet");
            return null;
        }
    }

    /**
     * Reads float[] from current read position
     * @return
     */
    public float[] ReadFloatArray() {
        if (buffer.size() > readPos) {
            int length = ReadInt();
            float[] values = new float[length];

            for (int i = 0; i < values.length; i++) {
                values[i] = ReadFloat();
            }

            return values;
        } else {
            Console.Error("Couldn't read type float[] from packet");
            return null;
        }
    }

    /**
     * Writes the packet length to the buffer
     */
    public void WriteLength() {
        InsertRange(ByteUtility.GetBytes(buffer.size()), 0);
    }

    /**
     * Returns length - read position
     * @return
     */
    public int UnreadLength() {
        return Length() - readPos;
    }

    /**
     * Size of buffer
     * @return
     */
    public int Length() {
        return buffer.size();
    }

    /**
     * Converts the buffer to a byte[]
     * @return
     */
    public byte[] ToArray() {
        readableBuffer = ToByteArray(buffer.toArray());
        return readableBuffer;
    }

    private void AddRange(byte[] data) {
        for (byte b : data) {
            buffer.add(b);
        }
    }

    private void InsertRange(byte[] data, int index) {
        for (int i = data.length - 1; i >= 0; i--) {
            buffer.add(index, data[i]);
        }
    }

    private byte[] ToByteArray(Object[] array) {
        byte[] byteArray = new byte[array.length];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte)array[i];
        }

        return byteArray;
    }

}