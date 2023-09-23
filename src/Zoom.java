import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Zoom {
    public static void main(String[] args) throws IOException {
        String imagePath = "";

        if (args.length > 0) {
            imagePath = args[0];
        } else {
            imagePath = "Linux5.bmp";
//            throw new IllegalArgumentException("Введите название файла");
        }

        BufferedImage myPicture = ImageIO.read(new File(imagePath));

        int width = myPicture.getWidth();
        int height = myPicture.getHeight();
        Raster data = myPicture.getData();
        int[] data1 = data.getPixels(0, 0, width, height, new int[3 * width * height]);
        Color[][] imageBitMap = new Color[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < 3 * width; j += 3) {
                int ptr = 3 * width * i + j;
                imageBitMap[i][j / 3] = new Color(data1[ptr], data1[ptr + 1], data1[ptr + 2]);
            }
        }

        int x0 = 1100;
        int y0 = 620;
        int x1 = 1600;
        int y1 = 900;
        int zoom = 5;

        Color newColor[][] = new Color[zoom * (y1 - y0)][zoom * (x1 - x0)];

        for (int i = 0; i < newColor.length; i++) {
            for (int j = 0; j < newColor[0].length; j++) {
                int x = x0 + j / zoom;
                int y = y0 + i / zoom;

                double u = (double) (i % zoom) / zoom;
                double v = (double) (j % zoom) / zoom;

                int tmpX = x + 1 != imageBitMap[0].length ? x + 1 : x;
                int tmpY = y + 1 != imageBitMap.length ? y + 1 : y;

                Color colorA = imageBitMap[y][x];
                Color colorB = imageBitMap[y][tmpX];
                Color colorC = imageBitMap[tmpY][tmpX];
                Color colorD = imageBitMap[tmpY][x];

                Color colorM = new Color(
                        (int) ((1 - u) * colorA.red + u * colorB.red),
                        (int) ((1 - u) * colorA.green + u * colorB.green),
                        (int) ((1 - u) * colorA.blue + u * colorB.blue)
                );
                Color colorN = new Color(
                        (int) ((1 - u) * colorD.red + u * colorC.red),
                        (int) ((1 - u) * colorD.green + u * colorC.green),
                        (int) ((1 - u) * colorD.blue + u * colorC.blue)
                );
                Color colorP = new Color(
                        (int) ((1 - v) * colorM.red + v * colorN.red),
                        (int) ((1 - v) * colorM.green + v * colorN.green),
                        (int) ((1 - v) * colorM.blue + v * colorN.blue)
                );

                newColor[i][j] = colorP;
            }
        }

        int newHeight = newColor.length;
        int newWidth = newColor[0].length;

        int[] resImg = new int[3 * newWidth * newHeight];

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < 3 * newWidth; j += 3) {
                int ptr = 3 * newWidth * i + j;
                Color color = newColor[i][j / 3];
                resImg[ptr] = color.red;
                resImg[ptr + 1] = color.green;
                resImg[ptr + 2] = color.blue;
            }
        }

        WritableRaster compatibleWritableRaster = data.createCompatibleWritableRaster(0, 0, newWidth, newHeight);
        compatibleWritableRaster.setPixels(0, 0, newWidth, newHeight, resImg);
        BufferedImage bufferedImage1 = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        bufferedImage1.setData(compatibleWritableRaster);

        ImageIO.write(bufferedImage1,
                "bmp",
                new File(imagePath.split("\\.")[0] + "-scaled.bmp")
        );
    }

    static class Color {
        int red;
        int green;
        int blue;

        public Color(int red, int green, int blue) {
            this.red = red;
            this.blue = blue;
            this.green = green;
        }
    }
}
























