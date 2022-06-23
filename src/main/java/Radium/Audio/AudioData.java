package Radium.Audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import RadiumEditor.Console;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

public class AudioData {

    final int format;
    final int samplerate;
    final int totalBytes;
    final int bytesPerFrame;
    final ByteBuffer data;

    private final AudioInputStream audioStream;
    private final byte[] dataArray;

    private AudioData(AudioInputStream stream) {
        this.audioStream = stream;
        AudioFormat audioFormat = stream.getFormat();
        format = GetOpenALFormat(audioFormat.getChannels(), audioFormat.getSampleSizeInBits());
        this.samplerate = (int) audioFormat.getSampleRate();
        this.bytesPerFrame = audioFormat.getFrameSize();
        this.totalBytes = (int) (stream.getFrameLength() * bytesPerFrame);
        this.data = BufferUtils.createByteBuffer(totalBytes);
        this.dataArray = new byte[totalBytes];
        LoadData();
    }

    protected void Dispose() {
        try {
            audioStream.close();
            data.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ByteBuffer LoadData() {
        try {
            int bytesRead = audioStream.read(dataArray, 0, totalBytes);
            data.clear();
            data.put(dataArray, 0, bytesRead);
            data.flip();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't read bytes from audio stream!");
        }
        return data;
    }


    public static AudioData Create(File file) {
        try {
            InputStream stream = new FileInputStream(file);
            InputStream bufferedInput = new BufferedInputStream(stream);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInput);
            return new AudioData(audioStream);
        } catch (Exception e) {
            Console.Error(e);
            return null;
        }
    }


    private static int GetOpenALFormat(int channels, int bitsPerSample) {
        if (channels == 1) {
            return bitsPerSample == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16;
        } else {
            return bitsPerSample == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16;
        }
    }

}
