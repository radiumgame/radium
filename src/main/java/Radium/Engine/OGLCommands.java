package Radium.Engine;

import java.util.ArrayList;
import java.util.List;

public class OGLCommands {

    public static List<Runnable> commands = new ArrayList<>();

    public static void RunCommands() {
        for (Runnable command : commands) {
            command.run();
        }

        commands.clear();
    }

}
