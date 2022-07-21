package Radium.Engine.System;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NativeFileDialog;

import java.io.File;

/**
 * A native file explorer to create and select file
 */
public class FileExplorer {

    protected FileExplorer() {}

    /**
     * Create a file using a native file explorer
     * @param extension File extension
     * @return Created file
     */
    public static String Create(String extension) {
        PointerBuffer outPath = MemoryUtil.memAllocPointer(1);

        try {
            NativeFileDialog.NFD_SaveDialog(extension, null, outPath);
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

    public static String Create(String extension, String defaultPath) {
        PointerBuffer outPath = MemoryUtil.memAllocPointer(1);

        try {
            NativeFileDialog.NFD_SaveDialog(extension, defaultPath, outPath);
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

    /**
     * File choose dialog
     * @param extensions Filtered file extensions
     * @return Chosen file path
     */
    public static String Choose(String extensions) {
        PointerBuffer outPath = MemoryUtil.memAllocPointer(1);
        int res = NativeFileDialog.NFD_OpenDialog(extensions, null, outPath);
        if (res != NativeFileDialog.NFD_OKAY) {
            return "";
        }

        String result = "";
        try {
            result = outPath.getStringUTF8();
            MemoryUtil.memFree(outPath);
        } catch (Exception e) {
            MemoryUtil.memFree(outPath);
            return null;
        }

        return result;
    }

    /**
     * Directory choose dialog
     * @return Chosen file path
     */
    public static String ChooseDirectory() {
        PointerBuffer outPath = MemoryUtil.memAllocPointer(1);
        int res = NativeFileDialog.NFD_PickFolder("", outPath);
        if (res != NativeFileDialog.NFD_OKAY) {
            return "";
        }

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

    public static boolean IsPathValid(String path) {
        return !path.equals("");
    }

}
