package Radium.UI.Legacy.Text;

import org.joml.Vector2f;

/**
 * Information about a charater in a font
 */
public class CharInfo {

    /**
     * X position on bitmap
     */
    public int sourceX;
    /**
     * Y position on bitmap
     */
    public int sourceY;
    /**
     * Width of character on bitmap
     */
    public int width;
    /**
     * Height of character on bitmap
     */
    public int height;

    /**
     * Texture coordinates for rendering texture
     */
    public Vector2f[] textureCoordinates = new Vector2f[2];

    /**
     * Create character information from sources
     */
    public CharInfo(int sourceX, int sourceY, int width, int height) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.width = width;
        this.height = height;
    }

    /**
     * Get usable texture coordinates from font bitmap
     * @param fontWidth Bitmap width
     * @param fontHeight Bitmap height
     */
    public void CalculateTextureCoordinates(int fontWidth, int fontHeight) {
        float x0 = (float)sourceX / (float)fontWidth;
        float x1 = (float)(sourceX + width) / (float)fontWidth;
        float y0 = (float)(sourceY - height) / (float)fontHeight;
        float y1 = (float)sourceY / (float)fontHeight;

        textureCoordinates[0] = new Vector2f(x0, y1);
        textureCoordinates[1] = new Vector2f(x1, y0);
    }

}
