package Engine.Components.Audio;

import Editor.Console;
import Engine.Audio.AudioType;
import Engine.Color;
import Engine.Component;
import Engine.Graphics.Texture;
import Engine.Objects.GameObject;
import imgui.ImGui;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.openal.*;
import org.lwjgl.system.libc.LibCStdlib;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Source extends Component {

    public String audioPath = "EngineAssets/Audio/demo.ogg";
    public float audioPitch = 1f;
    public boolean loop = false;

    private transient int bufferID = -1;
    private transient int sourceID = -1;

    private transient boolean isPlaying = false;

    public Source() {
        icon = new Texture("EngineAssets/Editor/Icons/source.png").textureID;
        RunInEditMode = true;
    }

    @Override
    public void Start() {

    }

    @Override
    public void Update() {
        if (isPlaying) {
            int state = AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE);
            if (state == AL10.AL_STOPPED) {
                isPlaying = false;
            }
        }
    }

    @Override
    public void OnAdd() {
        LoadAudio();
    }

    @Override
    public void OnRemove() {
        Destroy();
    }

    @Override
    public void OnVariableUpdate() {
        LoadAudio();
    }

    @Override
    public void GUIRender() {

    }

    public void Destroy() {
        AL10.alDeleteSources(sourceID);
        AL10.alDeleteBuffers(bufferID);
    }

    public void LoadAudio() {
        if (IsLoaded()) Destroy();

        if (!Files.exists(Paths.get(audioPath))) {
            return;
        }

        sourceID = -1;
        bufferID = -1;

        MemoryStack.stackPush();
        IntBuffer channelsBuffer = MemoryStack.stackMallocInt(1);
        MemoryStack.stackPush();
        IntBuffer sampleRateBuffer = MemoryStack.stackMallocInt(1);

        ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_filename(audioPath, channelsBuffer, sampleRateBuffer);
        if (rawAudioBuffer == null) {
            MemoryStack.stackPop();
            MemoryStack.stackPop();

            return;
        }

        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();

        MemoryStack.stackPop();
        MemoryStack.stackPop();

        int format = -1;
        if (channels == 1) {
            format = AL10.AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL10.AL_FORMAT_STEREO16;
        }

        bufferID = AL10.alGenBuffers();
        AL10.alBufferData(bufferID, format, rawAudioBuffer, sampleRate);

        sourceID = AL10.alGenSources();

        AL10.alSourcei(sourceID, AL10.AL_BUFFER, bufferID);
        AL10.alSourcei(sourceID, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
        AL10.alSourcef(sourceID, AL10.AL_GAIN, 1f);
        AL10.alSourcef(sourceID, AL10.AL_PITCH, audioPitch);

        LibCStdlib.free(rawAudioBuffer);

        isPlaying = false;
    }

    public void Play() {
        if (IsLoaded()) {
            int state = AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE);
            if (state == AL10.AL_STOPPED) {
                isPlaying = false;
            }

            if (!isPlaying) {
                AL10.alSourcePlay(sourceID);
                isPlaying = true;
            }
        } else {
            Console.Error("Source contains an invalid id. Check the path to your audio file.");
        }
    }

    public void Stop() {
        if (isPlaying) {
            AL10.alSourceStop(sourceID);
            isPlaying = false;
        }
    }

    public void Pause() {
        if (isPlaying) {
            AL10.alSourcePause(sourceID);
            isPlaying = false;
        }
    }



    private boolean IsLoaded() {
        return sourceID != -1 && bufferID != -1;
    }

}
