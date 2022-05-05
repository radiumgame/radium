package Radium.System;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.swing.*;

public class Popup {

    protected Popup() {}

    public static void MessagePopup(String message) {
        //JFrame frame = new JFrame();
        //JOptionPane.showMessageDialog(frame, message, "Radium", JOptionPane.PLAIN_MESSAGE);

        TinyFileDialogs.tinyfd_messageBox("Radium", message, "info", "ok", true);
    }

    public static void WarningPopup(String message) {
        TinyFileDialogs.tinyfd_messageBox("Radium", message, "", "warning", true);
    }

    public static void ErrorPopup(String message) {
        //JFrame frame = new JFrame();
        //JOptionPane.showMessageDialog(frame, message, "Radium", JOptionPane.ERROR_MESSAGE);

        TinyFileDialogs.tinyfd_messageBox("Radium", message, "", "error", true);
    }

    public static boolean Confirm(String message) {
        return TinyFileDialogs.tinyfd_messageBox("Radium", message, "okcancel", "ok", true);
    }

    public static boolean YesNo(String message) {
        return TinyFileDialogs.tinyfd_messageBox("Radium", message, "yesno", "", true);
    }

}
