package com.kosiarz.pixelart.filter;

import com.kosiarz.pixelart.annotation.LogExecutionTime;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class GaussianFilterService {

    @LogExecutionTime("Gaussian Blur")
    public BufferedImage appyFilter(BufferedImage originalImage, int radius, double sigma) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        double[][] kernel = generateGaussianKernel(radius, sigma);

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double redSum = 0.0;
                double greenSum = 0.0;
                double blueSum = 0.0;

                // Loop through the neighbors of pixel (x, y)
                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {

                        // Prevent going out of image bounds (clamping coordinates)
                        int neighborX = Math.clamp(x + kx, 0, width - 1);
                        int neighborY = Math.clamp(y + ky, 0, height - 1);

                        // Pixel is saved as 32-bit integer with ARGB channels - 8 bit channel each
                        int rgb = originalImage.getRGB(neighborX, neighborY);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;

                        double weight = kernel[ky + radius][kx + radius];

                        redSum += r * weight;
                        greenSum += g * weight;
                        blueSum += b * weight;
                    }
                }

                // Clamp again because rounding can generate out of bound coordinate
                int finalR = Math.clamp((int) Math.round(redSum), 0, 255);
                int finalG = Math.clamp((int) Math.round(greenSum), 0, 255);
                int finalB = Math.clamp((int) Math.round(blueSum), 0, 255);

                int finalRgb = (finalR << 16) | (finalG << 8) | finalB;

                outputImage.setRGB(x, y, finalRgb);
            }
        }

        return outputImage;
    }

    /**
     * Method calculates kernel used to apply gaussian blur.
     *
     * @param radius defines size of the kernel (width = 2*radius + 1)
     * @param sigma defines standard deviation of the Gaussian - bigger value means flatter curve
     * @return generated kernel for gaussian blur
     */
    private double[][] generateGaussianKernel(int radius, double sigma) {
        int size = 2 * radius + 1;
        double[][] kernel = new double[size][size];
        double sum = 0.0;

        // Standard 2D Gaussian Distribution Formula
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                double exponent = -(x * x + y * y) / (2 * sigma * sigma);
                double weight = Math.exp(exponent) / (2 * Math.PI * sigma * sigma);

                kernel[y + radius][x + radius] = weight;
                sum += weight;
            }
        }

        // Normalize the kernel so all weights add up to exactly 1.0 - this preserves image brightness.
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                kernel[y][x] /= sum;
            }
        }

        return kernel;
    }
}
