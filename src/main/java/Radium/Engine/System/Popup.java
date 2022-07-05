package Radium.Engine.System;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class Popup {

    protected Popup() {}

    public static void MessagePopup(String message) {
        //JFrame frame = new JFrame();
        //JOptionPane.showMessageDialog(frame, message, "Radium", JOptionPane.PLAIN_MESSAGE);

        TinyFileDialogs.tinyfd_messageBox("RadiumEngine", message, "info", "ok", true);
    }

    public static void WarningPopup(String message) {
        TinyFileDialogs.tinyfd_messageBox("RadiumEngine", message, "", "warning", true);
    }

    public static void ErrorPopup(String message) {
        //JFrame frame = new JFrame();
        //JOptionPane.showMessageDialog(frame, message, "Radium", JOptionPane.ERROR_MESSAGE);

        TinyFileDialogs.tinyfd_messageBox("RadiumEngine", message, "", "error", true);
    }

    public static boolean Confirm(String message) {
        return TinyFileDialogs.tinyfd_messageBox("RadiumEngine", message, "okcancel", "ok", true);
    }

    public static boolean YesNo(String message) {
        return TinyFileDialogs.tinyfd_messageBox("RadiumEngine", message, "yesno", "", true);
    }

}
