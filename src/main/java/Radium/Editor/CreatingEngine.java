package Radium.Editor;

import Radium.Editor.JFrame.JPictureBox;

import javax.swing.*;

public class CreatingEngine {

    private static JFrame frame;
    private static final int width = 450, height = 450;
    private static final String Image = "EngineAssets/Textures/Icon/bootup.png";

    protected CreatingEngine() {}

    public static void OpenWindowMultiThread() {
        frame = new JFrame("Radium Engine");
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.setIconImage(new ImageIcon("EngineAssets/Textures/Icon/icon.png").getImage());

        JPictureBox image = new JPictureBox();
        image.setIcon(new ImageIcon(Image));
        frame.add(image);

        frame.setVisible(true);
    }

    public static void CloseWindowMultiThread() {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
    }

}
