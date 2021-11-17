package Engine.System;

import Editor.Console;
import Engine.Util.NonInstantiatable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NativeFileDialog;

public final class FileExplorer extends NonInstantiatable {

    public static String Choose(String extensions) {
        PointerBuffer outPath = MemoryUtil.memAllocPointer(1);

        try {
            NativeFileDialog.NFD_OpenDialog(extensions, null, outPath);
        } finally {
            String result = outPath.getStringUTF8();
            MemoryUtil.memFree(outPath);

            return result;
        }
    }

}
