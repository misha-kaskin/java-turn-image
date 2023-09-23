import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Zoom {
    private static int count = 1;

    public static void main(String[] args) throws IOException {
        String imagePath = "";

        if (args.length > 0) {
            imagePath = args[0];
        } else {
            imagePath = "Linux5.bmp";
        }

        int x0 = 1100;
        int y0 = 620;
        int x1 = 1600;
        int y1 = 900;
        int zoom = 5;

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

        Color newColor[][] = new Color[zoom * (y1 - y0)][zoom * (x1 - x0)];

        for (int i = 0; i < newColor.length; i++) {
            for (int j = 0; j < newColor[0].length; j++) {
                int x = x0 + j / zoom;
                int y = y0 + i / zoom;

                double u = (double) (i % zoom) / zoom;
                double v = (double) (j % zoom) / zoom;

                if (x == 0 || y == 0) {
                    newColor[i][j] = firstType(imageBitMap, x, y, u, v);
                } else if (x >= newColor[0].length - 2 || y >= newColor.length - 2) {
                    newColor[i][j] = firstType(imageBitMap, x, y, u, v);
                } else {
                    newColor[i][j] = secondType(imageBitMap, x, y, u, v);
                }
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

    static Color firstType(Color[][] imageBitMap, int x, int y, double u, double v) {
        int tmpX = Math.min(imageBitMap[0].length, x + 1);
        int tmpY = Math.min(imageBitMap.length, y + 1);

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

        return colorP;
    }

    static Color secondType(Color[][] imageBitMap, int x, int y, double u, double v) {
        Color a = getColor(imageBitMap[y - 1][x - 1],
                imageBitMap[y - 1][x],
                imageBitMap[y - 1][x + 1],
                imageBitMap[y - 1][x + 2],
                u);

        Color b = getColor(imageBitMap[y][x - 1],
                imageBitMap[y][x],
                imageBitMap[y][x + 1],
                imageBitMap[y][x + 2],
                u);

        Color c = getColor(imageBitMap[y + 1][x - 1],
                imageBitMap[y + 1][x],
                imageBitMap[y + 1][x + 1],
                imageBitMap[y + 1][x + 2],
                u);

        Color d = getColor(imageBitMap[y + 2][x - 1],
                imageBitMap[y + 2][x],
                imageBitMap[y + 2][x + 1],
                imageBitMap[y + 2][x + 2],
                u);

        Color res = getColor(a, b, c, d, v);
        return res;
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

    private static Color getColor(Color a, Color b, Color c, Color d, double u) {
        Matrix redMatrix = getMatrix(a.red, b.red, c.red, d.red);
        Matrix greenMatrix = getMatrix(a.green, b.green, c.green, d.green);
        Matrix blueMatrix = getMatrix(a.blue, b.blue, c.blue, d.blue);

        int red = (int) (redMatrix.a * Math.pow(1 + u, 3)
                + redMatrix.b * Math.pow(1 + u, 2)
                + redMatrix.c * (1 + u)
                + redMatrix.d);

        int green = (int) (greenMatrix.a * Math.pow(1 + u, 3)
                + greenMatrix.b * Math.pow(1 + u, 2)
                + greenMatrix.c * (1 + u)
                + greenMatrix.d);

        int blue = (int) (blueMatrix.a * Math.pow(1 + u, 3)
                + blueMatrix.b * Math.pow(1 + u, 2)
                + blueMatrix.c * (1 + u)
                + blueMatrix.d);

        red = Math.max(0, red);
        red = Math.min(255, red);

        blue = Math.max(0, blue);
        blue = Math.min(255, blue);

        green = Math.max(0, green);
        green = Math.min(255, green);

        return new Color(red, blue, green);
    }

    private static Matrix getMatrix(int color1, int color2, int color3, int color4) {
        double x1 = color2 - color1;
        double x2 = color3 - color1;
        double x3 = color4 - color1;

        double y1 = x2 - 8 * x1;
        double y2 = x3 - 27 * x1;

        double z1 = 9 * y1;
        double z2 = 2 * y2;

        double d = color1;
        double c = (z2 - z1) / 6;
        double b = (-y1 - 6 * c) / 4;
        double a = x1 - b - c;

        return new Matrix(a, b, c, d);
    }

    static class Matrix {
        double a;
        double b;
        double c;
        double d;

        public Matrix() {
        }

        public Matrix(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }
}
