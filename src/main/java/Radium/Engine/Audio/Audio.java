package Radium.Engine.Audio;

import Radium.Engine.Util.FileUtility;
import Radium.Editor.Console;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbisInfo;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memSlice;

/**
 * Master audio class. Used for initializing the speakers and device.
 */
public class Audio {

    protected Audio() {}

    private static long Device;
    private static long Context;

    private static boolean useTLC;

    public static void Initialize() {
        Device = alcOpenDevice((ByteBuffer)null);
        if (Device == NULL) {
            Console.Error("Failed to open an OpenAL device.");
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(Device);

        if (!deviceCaps.OpenALC10) {
            Console.Error("OpenAL device does not support OpenAL 1.0.");
        }

        if (deviceCaps.OpenALC11) {
            List<String> devices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
            if (devices == null) {
                CheckALCError(NULL);
            } else {
                for (int i = 0; i < devices.size(); i++) {
                    System.out.println(i + ": " + devices.get(i));
                }
            }
        }

        String defaultDeviceSpecifier = Objects.requireNonNull(alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER));
        System.out.println("Default device: " + defaultDeviceSpecifier);

        System.out.println("ALC device specifier: " + alcGetString(Device, ALC_DEVICE_SPECIFIER));

        Context = alcCreateContext(Device, (IntBuffer)null);
        CheckALCError(Device);

        useTLC = deviceCaps.ALC_EXT_thread_local_context && alcSetThreadContext(Context);
        if (!useTLC) {
            if (!alcMakeContextCurrent(Context)) {
                Console.Error("Failed to make context current.");
            }
        }
        CheckALCError(Device);

        ALCapabilities caps = AL.createCapabilities(deviceCaps);
    }

    public static int LoadAudio(String path) {
        String extension = FileUtility.GetFileExtension(new File(path));
        if (extension.equals("ogg")) {
            return LoadOGG(path);
        } else if (extension.equals("wav")) {
            return LoadWithAudioData(path);
        }

        return 0;
    }

    public static int LoadOGG(String path) {
        int buffer = alGenBuffers();
        CheckALError();

        int source = alGenSources();
        CheckALError();

        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = ReadVorbis(path, 32 * 1024, info);

            alBufferData(buffer, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
            CheckALError();
        }

        alSourcei(source, AL_BUFFER, buffer);
        CheckALError();

        return source;
    }

    public static int LoadWithAudioData(String path) {
        int buffer = alGenBuffers();
        CheckALError();

        int source = alGenSources();
        CheckALError();

        try {
            AudioData dat = AudioData.Create(new File(path));
            alBufferData(buffer, dat.format, dat.data, dat.samplerate);
            CheckALError();
        } catch (Exception e) {
            Console.Error(e);
            return 0;
        }

        alSourcei(source, AL_BUFFER, buffer);
        CheckALError();

        return source;
    }

    public static void Mp3ToWav(File source) {
        try {
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("pcm_s16le");
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("wav");
            attrs.setAudioAttributes(audio);
            Encoder encoder = new Encoder();
            encoder.encode(source, new File(source.getPath().replace(".mp3", ".wav")), attrs);
            source.delete();
        } catch (Exception e) {
            Console.Error(e);
        }
    }

    public static float GetLength(File file) {
        try {
            if (FileUtility.GetFileExtension(file).equals("ogg")) {
                return GetOGGDuration(file);
            }

            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();

            String extension = FileUtility.GetFileExtension(file);
            if (extension.equals("wav")) {
                return stream.getFrameLength() / format.getFrameRate();
            } else {
                return GetOGGDuration(file);
            }
        } catch (Exception e) {
            Console.Error(e);
            return 0;
        }
    }

    public static void SetPosition(int source, float x, float y, float z) {
        alSource3f(source, AL_POSITION, x, y, z);
        CheckALError();
    }

    public static void PlayAudio(int source) {
        alSourcePlay(source);
        CheckALError();
    }

    private static float GetOGGDuration(File file) throws Exception {
        int rate = -1;
        int length = -1;

        int size = (int) file.length();
        byte[] t = new byte[size];

        FileInputStream fs = new FileInputStream(file);
        fs.read(t);

        for (int i = size - 1 - 8 - 2 - 4; i >= 0 && length < 0; i--) { //4 bytes for "OggS", 2 unused bytes, 8 bytes for length
            if (
                    t[i] == (byte) 'O'
                            && t[i + 1] == (byte) 'g'
                            && t[i + 2] == (byte) 'g'
                            && t[i + 3] == (byte) 'S'
            ) {
                byte[] byteArray = new byte[]{t[i + 6], t[i + 7], t[i + 8], t[i + 9], t[i + 10], t[i + 11], t[i + 12], t[i + 13]};
                ByteBuffer bb = ByteBuffer.wrap(byteArray);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                length = bb.getInt(0);
            }
        }
        for (int i = 0; i < size - 8 - 2 - 4 && rate < 0; i++) {
            if (
                    t[i] == (byte) 'v'
                            && t[i + 1] == (byte) 'o'
                            && t[i + 2] == (byte) 'r'
                            && t[i + 3] == (byte) 'b'
                            && t[i + 4] == (byte) 'i'
                            && t[i + 5] == (byte) 's'
            ) {
                byte[] byteArray = new byte[]{t[i + 11], t[i + 12], t[i + 13], t[i + 14]};
                ByteBuffer bb = ByteBuffer.wrap(byteArray);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                rate = bb.getInt(0);
            }

        }
        fs.close();

        double duration = (double)length / (double) rate;
        return (float) duration;
    }

    public static void Destroy() {
        alcMakeContextCurrent(NULL);
        if (useTLC) {
            AL.setCurrentThread(null);
        } else {
            AL.setCurrentProcess(null);
        }

        alcDestroyContext(Context);
        alcCloseDevice(Device);
    }

    private static ShortBuffer ReadVorbis(String resource, int bufferSize, STBVorbisInfo info) {
        try {
            ByteBuffer vorbis = null;
            try {
                vorbis = IoResourceToByteBuffer(resource, bufferSize);
            } catch (IOException e) {
                Console.Error(e);
            }

            IntBuffer error = BufferUtils.createIntBuffer(1);
            long decoder = stb_vorbis_open_memory(vorbis, error, null);
            if (decoder == NULL) {
                Console.Error("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();
            ShortBuffer pcm = BufferUtils.createShortBuffer(stb_vorbis_stream_length_in_samples(decoder) * channels);
            stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);

            stb_vorbis_close(decoder);

            return pcm;
        } catch (Exception e) {
            Console.Error(e);
            return ShortBuffer.allocate(1);
        }
    }

    private static void CheckALCError(long device) {
        int err = alcGetError(device);
        if (err != ALC_NO_ERROR) {
            Console.Error(alcGetString(device, err));
        }
    }

    private static void CheckALError() {
        int err = alGetError();
        if (err != AL_NO_ERROR) {
            Console.Error(alGetString(err));
        }
    }

    private static ByteBuffer ResizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static ByteBuffer IoResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer = null;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {

                }
            }
        } else {
            try (
                    InputStream source = Audio.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = ResizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            } catch (IOException e) {
                Console.Error("Failed to read resource: " + resource);
            }
        }

        buffer.flip();
        return memSlice(buffer);
    }

}
