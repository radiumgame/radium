package Radium.UI.NanoVG;

import RadiumEditor.Console;
import org.lwjgl.nanovg.NanoVG;

import java.util.ArrayList;
import java.util.List;

public class NVGRenderQueue {

    public static List<Runnable> renderQueue = new ArrayList<>();

    protected NVGRenderQueue() {}

    public static void Render() {
        for (Runnable r : renderQueue) {
            r.run();
        }

        renderQueue.clear();
    }

}
