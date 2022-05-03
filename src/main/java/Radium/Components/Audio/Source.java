package Radium.Components.Audio;

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

    private int source;
    private int audio;

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

        Vector3 camPos = Variables.DefaultCamera.gameObject.transform.WorldPosition();
        AL11.alListener3f(AL11.AL_POSITION, camPos.x, camPos.y, camPos.z);
    }

    
    public void Stop() {
        StopPlay();
    }
    
    public void OnAdd() {
        String path = FileExplorer.Choose("mp3,wav,ogg;");
        if (path != null) {
            audio = AudioUtility.LoadAudio(path);
            source = AudioUtility.CreateSource(audio);
        }
    }

    @Override
    public void GUIRender() {
        if (ImGui.button("Play")) {
            Play();

            float[] x = new float[1], y = new float[1], z = new float[1];
            AL11.alGetSource3f(source, AL11.AL_POSITION, x, y, z);
            Console.Log("Source: " + x[0] + " : " + y[0] + " : " + z[0]);

            float[] camX = new float[1], camY = new float[1], camZ = new float[1];
            AL11.alGetListener3f(AL11.AL_POSITION, camX, camY, camZ);
            Console.Log("Listener: " + camX[0] + " : " + camY[0] + " : " + camZ[0]);
        }
        ImGui.sameLine();
        if (ImGui.button("Stop")) {
            StopPlay();
        }
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
