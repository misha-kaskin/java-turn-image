import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Math.*;

public class TurnImage {
    public static void main(String[] args) throws IOException {
        /* Считывание из файла в двумерный массив */

        String imagePath = "";

        if (args.length > 0) {
            imagePath = args[0];
        } else {
            imagePath = "img.bmp";
        }

        BufferedImage myPicture = ImageIO.read(new File(imagePath));

        int width = myPicture.getWidth();
        int height = myPicture.getHeight();
        Raster data = myPicture.getData();
        int[] data1 = data.getPixels(0, 0, width, height, new int[3 * width * height]);
        int[][] imageBitMap = new int[height][3 * width];

        for (int i = 0; i < height; i++) {
            imageBitMap[i] = Arrays.copyOfRange(data1, 3 * i * width, 3 * (i + 1) * width);
        }

        /* Рассчет крайних точек (верхней, правой, левой, нижней) */

        Point high = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point low = new Point(-1, -1);
        Point left = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point right = new Point(-1, -1);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < 3 * width; j += 3) {
                if (imageBitMap[i][j] < 70
                        && imageBitMap[i][j + 1] < 70
                        && imageBitMap[i][j + 2] < 70) {
                    if (j > right.x) {
                        right.x = j;
                        right.y = i;
                    }
                    if (i > low.y) {
                        low.x = j;
                        low.y = i;
                    }
                    if (j < left.x) {
                        left.x = j;
                        left.y = i;
                    }
                    if (i < high.y) {
                        high.x = j;
                        high.y = i;
                    }
                }
            }
        }

        double sinAlpha = (right.x - low.x)
                / sqrt(pow((double) right.x / 3 - (double) low.x / 3, 2) + pow(right.y - low.y, 2)) / 3;
        double alpha = asin(sinAlpha);
        double tg = tan(alpha);

        int dlt = 100;

        high.y -= dlt;
        left.x -= dlt * 3;
        right.x += dlt * 3;
        low.y += dlt;

        double xA = (left.x / tg / 3 + left.y - high.y + tg * high.x / 3) * 3
                / (tg + 1 / tg);
        double yA = tg * xA / 3 + high.y - tg * high.x / 3;

        double xB = (right.x / tg / 3 + right.y - high.y + tg * high.x / 3) * 3
                / (tg + 1 / tg);
        double yB = tg * xB / 3 + high.y - tg * high.x / 3;

        double xC = (-low.y + tg * low.x / 3 + right.x / tg / 3 + right.y) * 3
                / (tg + 1 / tg);
        double yC = tg * xC / 3 + low.y - tg * low.x / 3;

        double xD = (left.x / tg / 3 + left.y - low.y + tg * low.x / 3) * 3
                / (tg + 1 / tg);
        double yD = tg * xD / 3 + low.y - tg * low.x / 3;

        Point A = new Point((int) xA, (int) yA);
        Point B = new Point((int) xB, (int) yB);
        Point C = new Point((int) xC, (int) yC);
        Point D = new Point((int) xD, (int) yD);

        high = A;
        right = B;
        low = C;
        left = D;

        /* Расчет угла поворота и размеров исходного изображения */

        int srcImgWidth = (int) sqrt(pow(right.x / 3 - high.x / 3, 2) + pow(right.y - high.y, 2));
        int srcImgHeight = (int) sqrt(pow(left.x / 3 - high.x / 3, 2) + pow(left.y - high.y, 2));

        int[][] newImg = new int[srcImgHeight][3 * srcImgWidth];

        /* Поворот изображения */

        for (int i = 0; i < srcImgHeight; i++) {
            int startX = high.x - (int) (sqrt(pow(i, 2) / (1 + pow(tg, 2))) * 3 * tg);
            int startY = high.y + (int) (sqrt(pow(i, 2) / (1 + pow(tg, 2))));
            startX = max(startX, 0);
            startY = max(startY, 0);

            for (int j = 0; j < 3 * srcImgWidth; j++) {
                int dx = (int) sqrt(pow(j, 2) / (1 + pow(tg, 2)));
                int dy = (int) (sqrt(pow(j, 2) / (1 + pow(tg, 2))) * tg) / 3;
                newImg[i][j] = imageBitMap[startY + dy][startX + dx];
            }
        }

        /* Запись данных в файл */

        int[] resImg = new int[3 * srcImgHeight * srcImgWidth];

        for (int i = 0; i < srcImgHeight; i++) {
            for (int j = 0; j < 3 * srcImgWidth; j++) {
                resImg[3 * i * srcImgWidth + j] = newImg[i][j];
            }
        }

        WritableRaster compatibleWritableRaster = data.createCompatibleWritableRaster(0, 0, srcImgWidth, srcImgHeight);
        compatibleWritableRaster.setPixels(0, 0, srcImgWidth, srcImgHeight, resImg);
        BufferedImage bufferedImage1 = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
        bufferedImage1.setData(compatibleWritableRaster);

        ImageIO.write(bufferedImage1,
                "bmp",
                new File(imagePath.split("\\.")[0] + "-turned.bmp")
        );
    }

    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
