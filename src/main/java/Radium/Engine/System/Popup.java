package Radium.Engine.System;

import Radium.Editor.Console;
import Radium.Engine.Math.Random;
import Radium.Engine.Time;
import Radium.Engine.Util.ThreadUtility;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.nio.file.Paths;

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

    private static JFrame loading;
    private static LoadingWindow loadingWindow;
    private static int timeRunning;
    private static float startTime;
    public static void OpenLoadingBar(String message) {
        loading = new JFrame(message);
        loading.setSize(400, 100);
        loading.setLocationRelativeTo(null);
        loading.setAlwaysOnTop(true);
        loading.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        loading.setResizable(false);

        try {
            loading.setIconImage(ImageIO.read(new FileInputStream("EngineAssets/Textures/Icon/icon.png")));
        } catch (Exception e) { Console.Error(e); }

        loadingWindow = new LoadingWindow(message);
        loading.add(loadingWindow);

        loading.setVisible(true);

        startTime = Time.GetTime();
        ThreadUtility.Run(() -> {
            while (loading != null) {
                UpdateProgressBar();
            }
        });
    }

    public static void CloseLoadingBar() {
        loading.setVisible(false);
        loading.dispose();
        loading = null;
        timeRunning = 0;
    }

    private static void UpdateProgressBar() {
        float time = Time.GetTime();
        if (time - startTime > 1) {
            startTime = time;
            loading.setTitle(loadingWindow.message + " (Active for " + (int) (timeRunning) + "s)");
            timeRunning++;
        }

        loadingWindow.repaint();
    }

    private static class LoadingWindow extends Canvas {

        public float dst = 0;

        private final float addAmount = 15;
        private final float cutoff = 0.833333333f;
        private final float speed = 0.35f;

        private final String message;

        public LoadingWindow(String message) {
            this.message = message;
        }

        @Override
        public void paint(Graphics g) {
            dst += addAmount / 1000;
            if (dst >= cutoff - 0.01f) {
                dst = 0;
            }

            g.setColor(new Color(0.8f, 0.8f, 0.8f));
            g.fillRect(14, 15, 360, 30);

            g.setColor(Color.GREEN);
            g.fillRect(Math.round(14 + (360 * dst)), 15, 60, 30);

            try {
                Thread.sleep(((Float)(addAmount * (1 / speed))).longValue());
            } catch (Exception e) { Console.Error(e); }
        }

    }

}
