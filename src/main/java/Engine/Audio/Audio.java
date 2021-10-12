package Engine.Audio;

import Engine.Math.Vector.Vector3;
import Engine.Variables;
import org.lwjgl.openal.*;

public class Audio {

    private static long audioContext;
    private static long audioDevice;

    public static void Initialize() {
        String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = ALC10.alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = ALC10.alcCreateContext(audioDevice, attributes);
        ALC10.alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (!alCapabilities.OpenAL10) {
            assert false : "Audio library not supported.";
        }
    }

    public static void Update() {
        if (Variables.DefaultCamera == null) return;

        Vector3 camPos = Variables.DefaultCamera.gameObject.transform.position;
        AL10.alListener3f(AL10.AL_POSITION, camPos.x, camPos.y, camPos.z);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }

    public static void Destroy() {
        ALC10.alcCloseDevice(GetDevice());
        ALC10.alcDestroyContext(GetContext());
    }

    public static long GetContext() {
        return audioContext;
    }

    public static long GetDevice() {
        return audioDevice;
    }

}
