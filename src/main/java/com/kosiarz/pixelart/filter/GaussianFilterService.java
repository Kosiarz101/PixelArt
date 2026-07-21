package com.kosiarz.pixelart.filter;

import com.kosiarz.pixelart.annotation.LogExecutionTime;
import com.kosiarz.pixelart.service.KernelImageFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
@RequiredArgsConstructor
public class GaussianFilterService {

    private final KernelImageFilterService kernelImageFilterService;

    /**
     * Applies Gaussian blur to the provided image.
     *
     * @param originalImage original image to process
     * @param radius defines size of the kernel (width = 2*radius + 1)
     * @param sigma defines standard deviation of the Gaussian - bigger value means flatter curve
     * @return output image with filter applied
     */
    @LogExecutionTime("Gaussian Blur")
    public BufferedImage applyFilter(BufferedImage originalImage, int radius, double sigma) {
        double[][] kernel = generateGaussianKernel(radius, sigma);
        return kernelImageFilterService.applyFilter(originalImage, kernel);
    }

    @LogExecutionTime("Gaussian Blur multithreaded")
    public BufferedImage applyFilterMultiThreaded(BufferedImage originalImage, int radius, double sigma) {
        double[][] kernel = generateGaussianKernel(radius, sigma);
        return kernelImageFilterService.applyFilterMultiThreaded(originalImage, kernel);
    }

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
