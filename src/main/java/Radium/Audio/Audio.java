package Radium.Audio;

import org.lwjgl.openal.*;

/**
 * Master audio class. Used for initializing the speakers and device.
 */
public class Audio {

    private static long audioContext;
    private static long audioDevice;

    protected Audio() {}

    /**
     * Initialize the audio device/speaker and context
     */
    public static void Initialize() {
        String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = ALC10.alcOpenDevice(defaultDeviceName);

        int[] attributes = { 0 };
        audioContext = ALC10.alcCreateContext(audioDevice, attributes);
        ALC10.alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (!alCapabilities.OpenAL10) {
            assert false : "Audio library not supported.";
        }
    }

    /**
     * Update the audio
     */
    public static void Update() {

    }

    /**
     * Destroy the device/speaker and context
     */
    public static void Destroy() {
        ALC10.alcCloseDevice(GetDevice());
        ALC10.alcDestroyContext(GetContext());
    }

    /**
     * Returns the audio context
     * @return Audio context
     */
    public static long GetContext() {
        return audioContext;
    }

    /**
     * Returns the current device/speaker
     * @return Audio device/speaker
     */
    public static long GetDevice() {
        return audioDevice;
    }

}
