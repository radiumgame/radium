package Engine.Networking;

import Editor.Console;
import Engine.Color;
import Engine.Math.Transform;
import Engine.Math.Vector.Vector2;
import Engine.Math.Vector.Vector3;
import Engine.Util.ByteUtility;
import at.favre.lib.bytes.Bytes;

import java.util.List;
import java.util.ArrayList;

public class Packet {

    private List<Byte> buffer;
    private byte[] readableBuffer;
    private int readPos;

    public Packet() {
        buffer = new ArrayList<>();
        readPos = 0;
    }

    public Packet(int id) {
        buffer = new ArrayList<>();
        readPos = 0;

        Write(id);
    }

    public Packet(byte[] data) {
        buffer = new ArrayList<>();
        readPos = 0;

        SetBytes(data);
    }

    public void SetBytes(byte[] bytes) {
        Write(bytes);
        readableBuffer = ToByteArray(buffer.toArray());
    }

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

    public void Write(byte value) {
        buffer.add(value);
    }

    public void Write(byte[] value) {
        AddRange(value);
    }

    public void Write(int value) {
        byte[] bytes = ByteUtility.GetBytes(value);
        AddRange(bytes);
    }

    public void Write(float value) {
        byte[] bytes = ByteUtility.GetBytes(value);
        AddRange(bytes);
    }

    public void Write(boolean value) {
        byte[] bytes = ByteUtility.GetBytes(value);
        AddRange(bytes);
    }

    public void Write(String value) {
        Write(value.length());

        byte[] bytes = ByteUtility.GetBytes(value);
        AddRange(bytes);
    }

    public void Write(Vector2 value) {
        Write(value.x);
        Write(value.y);
    }

    public void Write(Vector3 value) {
        Write(value.x);
        Write(value.y);
        Write(value.z);
    }

    public void Write(Transform value) {
        Write(value.position.x);
        Write(value.position.y);
        Write(value.position.z);

        Write(value.rotation.x);
        Write(value.rotation.y);
        Write(value.rotation.z);

        Write(value.scale.x);
        Write(value.scale.y);
        Write(value.scale.z);
    }

    public void Write(Color value) {
        Write(value.ToVector3());
    }

    public void Write(int[] value) {
        Write(value.length);
        for (int integer : value) {
            Write(integer);
        }
    }

    public void Write(float[] value) {
        Write(value.length);
        for (float dec : value) {
            Write(dec);
        }
    }

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

    public Color ReadColor() {
        if (buffer.size() > readPos) {
            return Color.FromVector3(ReadVector3());
        } else {
            Console.Error("Couldn't read type Color from packet");
            return null;
        }
    }

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

    public void WriteLength() {
        InsertRange(ByteUtility.GetBytes(buffer.size()), 0);
    }

    public int UnreadLength() {
        return Length() - readPos;
    }

    public int Length() {
        return buffer.size();
    }

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