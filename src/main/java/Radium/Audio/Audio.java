package Radium.Audio;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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
            throw new IllegalStateException("Failed to open an OpenAL device.");
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(Device);

        if (!deviceCaps.OpenALC10) {
            throw new IllegalStateException();
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
                throw new IllegalStateException();
            }
        }
        CheckALCError(Device);

        ALCapabilities caps = AL.createCapabilities(deviceCaps);
    }

    public static int LoadAudio(String path) {
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

    public static void SetPosition(int source, float x, float y, float z) {
        alSource3f(source, AL_POSITION, x, y, z);
        CheckALError();
    }

    public static void PlayAudio(int source) {
        alSourcePlay(source);
        CheckALError();
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
        ByteBuffer vorbis;
        try {
            vorbis = IoResourceToByteBuffer(resource, bufferSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IntBuffer error   = BufferUtils.createIntBuffer(1);
        long      decoder = stb_vorbis_open_memory(vorbis, error, null);
        if (decoder == NULL) {
            throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
        }

        stb_vorbis_get_info(decoder, info);

        int channels = info.channels();

        ShortBuffer pcm = BufferUtils.createShortBuffer(stb_vorbis_stream_length_in_samples(decoder) * channels);

        stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
        stb_vorbis_close(decoder);

        return pcm;
    }

    private static void CheckALCError(long device) {
        int err = alcGetError(device);
        if (err != ALC_NO_ERROR) {
            throw new RuntimeException(alcGetString(device, err));
        }
    }

    private static void CheckALError() {
        int err = alGetError();
        if (err != AL_NO_ERROR) {
            throw new RuntimeException(alGetString(err));
        }
    }

    private static ByteBuffer ResizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static ByteBuffer IoResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

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
                throw new IOException("Failed to read resource: " + resource, e);
            }
        }

        buffer.flip();
        return memSlice(buffer);
    }

}
