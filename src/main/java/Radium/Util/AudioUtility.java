package Radium.Util;

import RadiumEditor.Console;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL11;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

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

public class AudioUtility {

    protected AudioUtility() {}

    public static int LoadAudio(String filename) {
        int buffer = AL11.alGenBuffers();

        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = ReadAudio(filename, 32 * 1024, info);
            AL11.alBufferData(buffer, info.channels() == 1 ? AL11.AL_FORMAT_MONO16 : AL11.AL_FORMAT_STEREO16, pcm, info.sample_rate());
        }

        return buffer;
    }

    public static int CreateSource() {
        return AL11.alGenSources();
    }

    public static int CreateSource(int buffer) {
        int source = AL11.alGenSources();
        AL11.alSourcei(source, AL11.AL_BUFFER, buffer);
        return source;
    }

    private static ShortBuffer ReadAudio(String resource, int bufferSize, STBVorbisInfo info) {
        ByteBuffer vorbis = null;
        try {
            vorbis = IoResourceToByteBuffer(resource, bufferSize);
        } catch (Exception e) {
            Console.Error(e);
        }

        IntBuffer error = BufferUtils.createIntBuffer(1);
        long decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
        if (decoder == 0L) {
            Console.Error("Failed to load audio file: " + error);
        }
        STBVorbis.stb_vorbis_get_info(decoder, info);

        int channels = info.channels();
        ShortBuffer pcm = BufferUtils.createShortBuffer(STBVorbis.stb_vorbis_stream_length_in_samples(decoder) * channels);
        STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
        STBVorbis.stb_vorbis_close(decoder);

        return pcm;
    }

    private static ByteBuffer IoResourceToByteBuffer(String resource, int bufferSize) throws Exception {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {

                }
            }
        } else {
            try (
                    InputStream source = AudioUtility.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = BufferUtils.createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = ResizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }

        buffer.flip();
        return MemoryUtil.memSlice(buffer);
    }

    private static ByteBuffer ResizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

}
