package com.kosiarz.pixelart.filter;

import com.kosiarz.pixelart.annotation.LogExecutionTime;
import com.kosiarz.pixelart.service.KernelImageFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
@RequiredArgsConstructor
public class LaplacianFilterService {

    private final KernelImageFilterService kernelImageFilterService;

    double[][] kernel = new double[][]{
            {0, -1, 0}, {-1, 5, -1}, {0, -1, 0}
    };

    /**
     * Applies Laplacian filter which sharpens the provided image.
     *
     * @param originalImage original image to process
     * @return output image with filter applied
     */
    @LogExecutionTime("Laplacian filter")
    public BufferedImage applyFilter(BufferedImage originalImage) {
        return kernelImageFilterService.applyFilter(originalImage, kernel);
    }

    @LogExecutionTime("Laplacian filter multithreaded")
    public BufferedImage applyFilterMultiThreaded(BufferedImage originalImage) {
        return kernelImageFilterService.applyFilterMultiThreaded(originalImage, kernel);
    }
}
