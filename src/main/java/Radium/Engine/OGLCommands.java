package Radium.Engine;

import java.util.ArrayList;
import java.util.List;

public class OGLCommands {

    public static List<Runnable> commands = new ArrayList<>();

    public static void RunCommands() {
        for (int i = 0; i < commands.size(); i++) {
            commands.get(i).run();
        }

        commands.clear();
    }

}
