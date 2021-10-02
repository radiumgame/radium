package Engine.Math.Noise;

import Engine.Util.NonInstantiatable;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class NoiseImageWriter extends NonInstantiatable {

    public static void GreyWriteImage(String filepath, double[][] data){
        BufferedImage image = new BufferedImage(data.length,data[0].length, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < data[0].length; y++)
        {
            for (int x = 0; x < data.length; x++)
            {
                if (data[x][y] > 1){
                    data[x][y] = 1;
                }
                if (data[x][y]<0){
                    data[x][y] = 0;
                }
                Color col = new Color((float)data[x][y],(float)data[x][y],(float)data[x][y]);
                image.setRGB(x, y, col.getRGB());
            }
        }

        try {
            File outputfile = new File(filepath + "noise.png");
            outputfile.createNewFile();

            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write noise data.");
        }
    }

    public static void GreyWriteImage(String filepath, float[][] data){
        BufferedImage image = new BufferedImage(data.length,data[0].length, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < data[0].length; y++)
        {
            for (int x = 0; x < data.length; x++)
            {
                if (data[x][y] > 1){
                    data[x][y] = 1;
                }
                if (data[x][y]<0){
                    data[x][y] += 1;
                }
                Color col = new Color((float)data[x][y],(float)data[x][y],(float)data[x][y]);
                image.setRGB(x, y, col.getRGB());
            }
        }

        try {
            File outputfile = new File(filepath + "noise.png");
            outputfile.createNewFile();

            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write noise data.");
        }
    }

}