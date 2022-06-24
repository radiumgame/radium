package Radium.Audio;

import Radium.Time;
import java.io.File;

import RadiumEditor.Console;
import org.lwjgl.openal.AL11;

public class AudioClip {

    public int source;
    public float length;
    public String formattedLength;
    public String formattedPlayingTime;

    public boolean playing = false;
    public float volume = 1;

    private float playingTime;
    public float position;

    public boolean dragging = false;

    public AudioClip(int source, File f) {
        this.source = source;
        AL11.alSourcei(source, AL11.AL_LOOPING, AL11.AL_FALSE);

        length = Audio.GetLength(f);

        int minutes = (int)(length - (length % 60)) / 60;
        int seconds = (int)length % 60;
        formattedLength = minutes + ":" + seconds;
    }

    public void Play() {
        float[] x = new float[1], y = new float[1], z = new float[1];
        AL11.alGetListener3f(AL11.AL_POSITION, x, y, z);
        AL11.alSource3f(source, AL11.AL_POSITION, x[0], y[0], z[0]);
        AL11.alSourcePlay(source);
        playing = true;
    }

    public void UpdateClip() {
        if (playing) {
            playingTime += Time.deltaTime;
        }
        if (playingTime > length) {
            Stop();
        }

        if (!dragging) {
            position = GetPlayingTime() / length;
        }

        int minutes = (int)(playingTime - (playingTime % 60)) / 60;
        int seconds = (int)playingTime % 60;
        formattedPlayingTime = minutes + ":" + ((seconds < 10) ? "0" : "") + seconds;
    }

    public void Stop() {
        AL11.alSourceStop(source);
        playing = false;

        playingTime = 0;
    }

    public void Pause() {
        playing = false;
        AL11.alSourcePause(source);
    }

    public void SetVolume(float volume) {
        this.volume = volume;
        AL11.alSourcef(source, AL11.AL_GAIN, volume);
    }

    public void SetPosition(float pos) {
        position = pos;
        AL11.alSourcef(source, AL11.AL_SEC_OFFSET, pos * length);
        playingTime = length * pos;
    }

    public float GetPlayingTime() {
        return playingTime;
    }

}
