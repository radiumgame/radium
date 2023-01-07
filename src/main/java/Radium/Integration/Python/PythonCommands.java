package Radium.Integration.Python;

import Radium.Editor.Console;
import Radium.Engine.Audio.Audio;
import Radium.Engine.Components.UI.Button;
import Radium.Engine.Input.Input;
import Radium.Engine.Objects.GameObject;
import Radium.Integration.Project.Project;
import org.python.core.PyObject;

import java.io.File;
import java.util.HashMap;

public class PythonCommands {

    private Allocation allocation;

    public PythonCommands(Allocation allocation) {
        this.allocation = allocation;
    }

    public PyObject RunCommand(String key, PyObject[] args) {
        // Input
        if (key.equals("MOUSE_X")) {
            return allocation.Float((float)Input.GetMouseX());
        } else if (key.equals("MOUSE_Y")) {
            return allocation.Float((float)Input.GetMouseY());
        } else if (key.equals("MOUSE_DELTA_X")) {
            return allocation.Float((float)Input.GetMouseDeltaX());
        } else if (key.equals("MOUSE_DELTA_Y")) {
            return allocation.Float((float)Input.GetMouseDeltaY());
        }

        // Audio
        else if (key.equals("LOAD_AUDIO")) {
            String audioPath = args[0].toString();
            File audioFile = new File(Project.Current().assets + "/" + audioPath);
            if (!audioFile.exists()) {
                return allocation.JavaData(allocation.Float(0), allocation.String("0:00"));
            }

            float length = Audio.GetLength(audioFile);
            int minutes = (int)(length - (length % 60)) / 60;
            int seconds = (int)length % 60;
            String formattedLength = minutes + ":" + seconds;
            return allocation.JavaData(allocation.Float(length), allocation.String(formattedLength));
        }

        // UI
        else if (key.equals("SET_BUTTON_CALLBACK")) {
            String goId = args[0].toString();
            PyObject button = args[1];

            GameObject go = GameObject.Find(goId);
            if (go == null) {
                Console.Error("Failed to set button callback");
                return new PyObject();
            }

            Button buttonComponent = go.GetComponent(Button.class);
            if (buttonComponent == null) {
                Console.Error("Failed to set button callback");
                return new PyObject();
            }

            buttonComponent.buttonPy = button;
            buttonComponent.interpreter = allocation.python;
        }

        return new PyObject();
    }

}
