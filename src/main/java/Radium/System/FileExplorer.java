package Radium.System;

import RadiumEditor.Console;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.io.File;

public class FileExplorer {

    protected FileExplorer() {}

    public static String Choose(String extensions) {
        PointerBuffer outPath = MemoryUtil.memAllocPointer(1);

        try {
            NativeFileDialog.NFD_OpenDialog(extensions, null, outPath);
        } finally {
            String result;

            try {
                result = outPath.getStringUTF8();
                MemoryUtil.memFree(outPath);
            } catch (Exception e) {
                MemoryUtil.memFree(outPath);
                return null;
            }

            return result;
        }
    }

}
