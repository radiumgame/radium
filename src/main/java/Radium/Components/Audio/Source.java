package Radium.Components.Audio;

import Radium.Audio.Audio;
import Radium.Math.Vector.Vector3;
import Radium.System.FileExplorer;
import Radium.Util.AudioUtility;
import Radium.Variables;
import RadiumEditor.Annotations.RunInEditMode;
import RadiumEditor.Console;
import Radium.Component;
import Radium.Graphics.Texture;
import Radium.PerformanceImpact;
import imgui.ImGui;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.openal.*;
import org.lwjgl.system.libc.LibCStdlib;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Loads and plays audio files
 */
@RunInEditMode
public class Source extends Component {

    public float pitch = 1;
    public float gain = 1;
    public boolean loop = false;

    private int source;

    /**
     * Generate an empty Source with no audio
     */
    public Source() {
        icon = new Texture("EngineAssets/Editor/Icons/source.png").textureID;

        name = "Source";
        description = "Loads and plays sounds";
        impact = PerformanceImpact.Low;
        submenu = "Audio";
    }

    
    public void Start() {
        Play();
    }

    
    public void Update() {
        Vector3 pos = gameObject.transform.WorldPosition();
        AL11.alSource3f(source, AL11.AL_POSITION, pos.x, pos.y, pos.z);
        AL11.alSource3f(source, AL11.AL_VELOCITY, 0, 0, 0);
    }

    
    public void Stop() {
        StopPlay();
    }
    
    public void OnAdd() {
        String path = FileExplorer.Choose("mp3,wav,ogg;");
        if (path != null) {
            source = Audio.LoadAudio(path);
        }
    }

    @Override
    public void UpdateVariable(String variableName) {
        AL11.alSourcef(source, AL11.AL_PITCH, pitch);
        AL11.alSourcef(source, AL11.AL_GAIN, gain);
        AL11.alSourcei(source, AL11.AL_LOOPING, loop ? AL11.AL_TRUE : AL11.AL_FALSE);
    }

    public void Play() {
        AL11.alSourcePlay(source);
    }

    public void Pause() {
        AL11.alSourcePause(source);
    }

    public void StopPlay() {
        AL11.alSourceStop(source);
    }

}
