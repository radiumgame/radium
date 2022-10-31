package Radium.Editor.JFrame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class JPictureBox extends JComponent {

    private Icon icon = null;
    private final Dimension dimension = new Dimension(100, 100);
    private Image image = null;
    private ImageIcon ii = null;
    private SizeMode sizeMode = SizeMode.STRETCH;
    private int newHeight, newWidth, originalHeight, originalWidth;

    public JPictureBox() {
        JPictureBox.this.setPreferredSize(dimension);
        JPictureBox.this.setOpaque(false);
        JPictureBox.this.setSizeMode(SizeMode.STRETCH);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (ii != null) {
            switch (getSizeMode()) {
                case NORMAL:
                    g.drawImage(image, 0, 0, ii.getIconWidth(), ii.getIconHeight(), null);
                    break;
                case ZOOM:
                    aspectRatio();
                    g.drawImage(image, 0, 0, newWidth, newHeight, null);
                    break;
                case STRETCH:
                    g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
                    break;
                case CENTER:
                    g.drawImage(image, (int) (this.getWidth() / 2) - (int) (ii.getIconWidth() / 2), (int) (this.getHeight() / 2) - (int) (ii.getIconHeight() / 2), ii.getIconWidth(), ii.getIconHeight(), null);
                    break;
                default:
                    g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
            }
        }
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        ii = (ImageIcon) icon;
        image = ii.getImage();
        originalHeight = ii.getIconHeight();
        originalWidth = ii.getIconWidth();
    }

    public SizeMode getSizeMode() {
        return sizeMode;
    }

    public void setSizeMode(SizeMode sizeMode) {
        this.sizeMode = sizeMode;
    }

    public enum SizeMode {
        NORMAL,
        STRETCH,
        CENTER,
        ZOOM
    }

    private void aspectRatio() {
        if (ii != null) {
            newHeight = this.getHeight();
        }
    }
}