import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Math.*;

public class TurnImage {
    static final int COLOR_SIZE = 3;

    public static void main(String[] args) throws IOException {
        /* Считывание из файла в двумерный массив */

        String imagePath = "";

        if (args.length > 0) {
            imagePath = args[0];
        } else {
            throw new IllegalArgumentException("Введите название файла");
        }

        BufferedImage myPicture = ImageIO.read(new File(imagePath));

        int width = myPicture.getWidth();
        int height = myPicture.getHeight();
        Raster data = myPicture.getData();
        int[] data1 = data.getPixels(0, 0, width, height, new int[3 * width * height]);
        int[][] imageBitMap = new int[height][COLOR_SIZE * width];

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
                if (imageBitMap[i][j] < 20
                        && imageBitMap[i][j + 1] < 20
                        && imageBitMap[i][j + 2] < 20) {
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

        double rightSinAlpha = (right.x - low.x)
                / sqrt(pow((double) right.x / 3 - (double) low.x / 3, 2) + pow(right.y - low.y, 2)) / 3;
        double rightAlpha = asin(rightSinAlpha);

        int x = 0;
        int y = 0;

        int dlt = 20;

        right.x = x = width * 3 - dlt;
        right.y = y = (int) (x / 3 * tan(rightAlpha));

        low.x = x = 3 * width - 3 * (int) ((height - dlt - y) * tan(rightAlpha));
        low.y = y = height - dlt;

        left.y = height - (int) ((x / 3 - dlt) * tan(rightAlpha));
        left.x = x = dlt;

        high.x = left.x + (right.x - low.x);
        high.y = left.y - (low.y - right.y);

        /* Расчет угла поворота и размеров исходного изображения */

        double alpha = rightAlpha;

        int srcImgWidth = (int) sqrt(pow(right.x / 3 - high.x / 3, 2) + pow(right.y - high.y, 2));
        int srcImgHeight = (int) sqrt(pow(left.x / 3 - high.x / 3, 2) + pow(left.y - high.y, 2));

        int[][] newImg = new int[srcImgHeight][3 * srcImgWidth];

        /* Поворот изображения */

        for (int i = 0; i < srcImgHeight; i++) {
            int startX = high.x - (int) (sqrt(pow(i, 2) / (1 + pow(tan(alpha), 2))) * 3 * tan(alpha));
            int startY = high.y + (int) sqrt(pow(i, 2) / (1 + pow(tan(alpha), 2)));

            for (int j = 0; j < 3 * srcImgWidth; j++) {
                int dx = (int) sqrt(pow(j, 2) / (1 + pow(tan(alpha), 2)));
                int dy = (int) (sqrt(pow(j, 2) / (1 + pow(tan(alpha), 2))) * tan(alpha)) / 3;
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
