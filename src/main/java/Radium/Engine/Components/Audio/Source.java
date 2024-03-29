package Radium.Engine.Components.Audio;

import Radium.Editor.Annotations.HideInEditor;
import Radium.Editor.Console;
import Radium.Editor.Files.Parser;
import Radium.Editor.Icons;
import Radium.Engine.Audio.Audio;
import Radium.Engine.Audio.AudioType;
import Radium.Engine.Math.Vector.Vector3;
import java.io.File;

import Radium.Engine.Util.AudioUtility;
import Radium.Engine.Variables;
import Radium.Editor.Annotations.ExecuteGUI;
import Radium.Editor.Annotations.RunInEditMode;
import Radium.Engine.Component;
import Radium.Engine.Graphics.Texture;
import Radium.Engine.PerformanceImpact;
import Radium.Editor.EditorGUI;
import Radium.Integration.Project.Project;
import org.lwjgl.openal.*;

/**
 * Loads and plays audio files
 */
@RunInEditMode
public class Source extends Component {

    @ExecuteGUI("AUDIO_CLIP")
    @HideInEditor
    public File audioClip;
    public AudioType audioType = AudioType.ThreeDimensional;
    public float pitch = 1;
    public float gain = 1;
    public boolean loop = false;
    public boolean playOnAwake = false;

    private int source = 0;

    /**
     * Generate an empty Source with no audio
     */
    public Source() {
        icon = new Texture("EngineAssets/Editor/Icons/source.png", true).GetTextureID();

        name = "Source";
        description = "Loads and plays sounds";
        impact = PerformanceImpact.Low;
        submenu = "Audio";
    }

    
    public void Start() {
        if (playOnAwake) {
            Play();
        }
    }

    
    public void Update() {
        if (audioType == AudioType.ThreeDimensional) {
            Vector3 pos = gameObject.transform.WorldPosition();
            AL11.alSource3f(source, AL11.AL_POSITION, pos.x, pos.y, pos.z);
            AL11.alSource3f(source, AL11.AL_VELOCITY, 0, 0, 0);
        } else {
            Vector3 camPos = Variables.DefaultCamera.gameObject.transform.WorldPosition();
            AL11.alSource3f(source, AL11.AL_POSITION, camPos.x, camPos.y, camPos.z);
        }
    }

    
    public void Stop() {
        StopPlay();
    }
    
    public void OnAdd() {
        if (audioClip != null) {
            source = Audio.LoadAudio(audioClip.getPath());
        }
    }

    @Override
    public void UpdateVariable(String variableName) {
        AL11.alSourcef(source, AL11.AL_PITCH, pitch);
        AL11.alSourcef(source, AL11.AL_GAIN, gain);
        AL11.alSourcei(source, AL11.AL_LOOPING, loop ? AL11.AL_TRUE : AL11.AL_FALSE);
    }

    private final static String[] allowedTypes = new String[] { "ogg", "wav" };

    @Override
    public void ExecuteGUI(String name) {
        if (name.equals("AUDIO_CLIP")) {
            File f = EditorGUI.FileReceive(allowedTypes, "Audio Clip", audioClip, "SourceAudioClip", Parser.audio, Icons.GetIcon("audio"));
            if (f != null) {
                audioClip = f;
                source = Audio.LoadAudio(audioClip.getPath());
            }
        }
    }

    public void SetAudioClip(String ac) {
        File audioClip = new File(Project.Current().assets + "/" + ac);
        if (!audioClip.exists()) {
            Console.Error("Invalid audio clip: " + ac);
            return;
        }
        this.audioClip = audioClip;
        source = Audio.LoadAudio(audioClip.getPath());
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
