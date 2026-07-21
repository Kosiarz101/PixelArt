package com.kosiarz.pixelart.controller;

import com.kosiarz.pixelart.filter.GaussianFilterService;
import com.kosiarz.pixelart.filter.LaplacianFilterService;
import com.kosiarz.pixelart.filter.SobelFilterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FiltersController {

    private static final Logger log = LoggerFactory.getLogger(FiltersController.class);

    private final GaussianFilterService gaussianFilterService;
    private final SobelFilterService sobelFilterService;
    private final LaplacianFilterService laplacianFilterService;

    @PostMapping("/gaussian-blur")
    public ResponseEntity<byte[]> applyGaussian(@RequestParam("image") MultipartFile file,
                                                @RequestParam(value = "radius") int radius,
                                                @RequestParam(value = "sigma") double sigma,
                                                @RequestParam(value = "multi-threaded", defaultValue = "false")
                                                    boolean multithreaded) {
        try {
            BufferedImage inputImage = ImageIO.read(file.getInputStream());

            BufferedImage blurredImage;
            if (multithreaded)
                blurredImage = gaussianFilterService.applyFilterMultiThreaded(inputImage, radius, sigma);
            else
                blurredImage = gaussianFilterService.applyFilter(inputImage, radius, sigma);

            ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
            ImageIO.write(blurredImage, "png", imageStream);
            byte[] imageBytes = imageStream.toByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);

        } catch (IOException e) {
            log.error("Error occurred while applying Gaussian filter: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/sobel")
    public ResponseEntity<byte[]> applySobel(@RequestParam("image") MultipartFile file,
                                             @RequestParam(value = "multi-threaded", defaultValue = "false")
                                             boolean multithreaded) {
        try {
            BufferedImage inputImage = ImageIO.read(file.getInputStream());

            BufferedImage blurredImage;
            blurredImage = sobelFilterService.applyFilter(inputImage);

            ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
            ImageIO.write(blurredImage, "png", imageStream);
            byte[] imageBytes = imageStream.toByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);

        } catch (IOException e) {
            log.error("Error occurred while applying Sobel filter: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/laplacian")
    public ResponseEntity<byte[]> applyLaplacian(@RequestParam("image") MultipartFile file,
                                                 @RequestParam(value = "multi-threaded", defaultValue = "false")
                                                 boolean multithreaded) {
        try {
            BufferedImage inputImage = ImageIO.read(file.getInputStream());

            BufferedImage blurredImage;
            if (multithreaded)
                blurredImage = laplacianFilterService.applyFilterMultiThreaded(inputImage);
            else
                blurredImage = laplacianFilterService.applyFilter(inputImage);

            ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
            ImageIO.write(blurredImage, "png", imageStream);
            byte[] imageBytes = imageStream.toByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);

        } catch (IOException e) {
            log.error("Error occurred while applying Laplacian filter: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
