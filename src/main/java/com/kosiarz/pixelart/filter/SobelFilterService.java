package com.kosiarz.pixelart.filter;

import com.kosiarz.pixelart.annotation.LogExecutionTime;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class SobelFilterService {

    private final int[] kernelHorizontal = new int[]{-1,0,1,-2,0,2,-1,0,1};
    private final int[] kernelVertical = new int[]{-1,-2,-1,0,0,0,1,2,1};

    @LogExecutionTime("Sobel Edge Detection")
    public BufferedImage applyFilter(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int finalPixel = calculatePixel(x, y, originalImage);
                outputImage.setRGB(x, y, finalPixel);
            }
        }

        return outputImage;
    }

    private int calculatePixel(int x, int y, BufferedImage originalImage) {
        double gx = 0.0;
        double gy = 0.0;
        int kernelCounter = 0;
        for (int ky = y+1; ky >= y-1; ky--) {
            for (int kx = x-1; kx <= x+1; kx++) {

                int neighborX = Math.clamp(kx, 0, originalImage.getWidth() - 1);
                int neighborY = Math.clamp(ky, 0, originalImage.getHeight() - 1);

                int rgb = originalImage.getRGB(neighborX, neighborY);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Standard human eye luminance weights defined by ITU-R
                double gray = 0.299 * r + 0.587 * g + 0.114 * b;

                double weightX = kernelHorizontal[kernelCounter];
                double weightY = kernelVertical[kernelCounter];

                gx += gray * weightX;
                gy += gray * weightY;
                kernelCounter++;
            }
        }

        int magnitude = (int) Math.round(Math.sqrt((gx * gx) + (gy * gy)));
        int finalGrad = Math.clamp(magnitude, 0, 255);

        return (finalGrad << 16) | (finalGrad << 8) | finalGrad;
    }
}
