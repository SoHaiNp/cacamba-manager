package com.eccolimp.cacamba_manager.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;

@RestController
public class FaviconController {

    @GetMapping(value = "/favicon.ico", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> faviconPng() throws IOException {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            // Fundo primário da marca
            g2.setColor(new Color(0x26, 0x59, 0x88));
            g2.fillRect(0, 0, 16, 16);
            // Marca simples (quadrado claro no canto)
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRect(3, 3, 6, 6);
        } finally {
            g2.dispose();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .body(out.toByteArray());
    }
}


