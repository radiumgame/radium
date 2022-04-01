package Radium.System;

import javax.swing.*;

public class Popup {

    protected Popup() {}

    public static void MessagePopup(String message) {
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, message, "Radium", JOptionPane.PLAIN_MESSAGE);
    }

    public static void WarningPopup(String message) {
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, message, "Radium", JOptionPane.WARNING_MESSAGE);
    }

    public static void ErrorPopup(String message) {
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, message, "Radium", JOptionPane.ERROR_MESSAGE);
    }

}
