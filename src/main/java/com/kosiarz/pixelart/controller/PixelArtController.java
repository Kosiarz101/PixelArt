package com.kosiarz.pixelart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/art")
public class PixelArtController {

    private static final Logger log = LoggerFactory.getLogger(PixelArtController.class);

    @PostMapping("/pixelate")
    public ResponseEntity<byte[]> processImage(@RequestParam("image") MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            if (originalImage == null) {
                return ResponseEntity.badRequest().body(null);
            }

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            BufferedImage processedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    Color originalColor = new Color(originalImage.getRGB(x, y));

                    int red = originalColor.getRed() / 2;
                    int green = originalColor.getGreen() / 2;
                    int blue = originalColor.getBlue() / 2;

                    Color newColor = new Color(red, green, blue);
                    processedImage.setRGB(x, y, newColor.getRGB());
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error occurred while processing image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
