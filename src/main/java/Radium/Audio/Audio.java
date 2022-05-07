package Radium.Audio;

import RadiumEditor.Console;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;

/**
 * Master audio class. Used for initializing the speakers and device.
 */
public class Audio {

    protected Audio() {}

    private static long audioDevice;
    private static long audioContext;

    private static boolean TLC;

    public static void Initialize() {
        audioDevice = ALC11.alcOpenDevice((CharSequence)null);
        if (audioDevice == 0L) {
            Console.Error("Failed to open audio device.");
            return;
        }

        ALCCapabilities deviceCapabilities = ALC.createCapabilities(audioDevice);
        if (!deviceCapabilities.OpenALC11) {
            Console.Error("Invalid OpenAL capabilities.");
            return;
        }

        if(deviceCapabilities.OpenALC11) {
            List<String> devices = ALUtil.getStringList(0L, ALC11.ALC_ALL_DEVICES_SPECIFIER);
            if (devices == null) {
                Console.Error("Failed to get audio devices.");
                return;
            }
        }

        String defaultDeviceSpecifier = Objects.requireNonNull(ALC11.alcGetString(0L, ALC11.ALC_DEFAULT_DEVICE_SPECIFIER));
        audioContext = ALC11.alcCreateContext(audioDevice, (IntBuffer)null);

        TLC = deviceCapabilities.ALC_EXT_thread_local_context && EXTThreadLocalContext.alcSetThreadContext(audioContext);
        if (!TLC) {
            if (!ALC11.alcMakeContextCurrent(audioContext)) {
                Console.Error("Failed to make audio context current.");
                return;
            }
        }
        ALCapabilities caps = AL.createCapabilities(deviceCapabilities);
    }

    public static void Update() {

    }

    public static void Destroy() {
        ALC11.alcMakeContextCurrent(0L);
        if (TLC) {
            AL.setCurrentThread(null);
        } else {
            AL.setCurrentProcess(null);
        }
        ALC11.alcDestroyContext(audioContext);
        ALC11.alcCloseDevice(audioDevice);
    }

}
