package com.decade.practice.utils;

import com.decade.practice.models.domain.embeddable.ImageSpec;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

        private ImageUtils() {
                // Private constructor to prevent instantiation
        }

        public static BufferedImage cropCenter(BufferedImage image) throws IOException {
                int h = image.getHeight();
                int w = image.getWidth();
                int d = Math.min(w, h);
                return image.getSubimage((w - d) / 2, (h - d) / 2, d, d);
        }

        public static BufferedImage crop(
                BufferedImage image,
                int w,
                int h
        ) throws IOException {
                BufferedImage cropped = cropCenter(image);
                return scaleImage(cropped, w, h);
        }

        public static BufferedImage crop(
                InputStream inputStream,
                int w,
                int h
        ) throws IOException {
                BufferedImage cropped = cropCenter(ImageIO.read(inputStream));
                return scaleImage(cropped, w, h);
        }

        public static BufferedImage crop(InputStream inputStream) throws IOException {
                return crop(inputStream, ImageSpec.DEFAULT_WIDTH, ImageSpec.DEFAULT_HEIGHT);
        }

        public static BufferedImage scaleImage(BufferedImage image, int w, int h) throws IOException {
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();

                if (imageWidth <= w && imageHeight <= h) {
                        return image;
                }

                float scaleWidth = (float) w / imageWidth;
                float scaleHeight = (float) h / imageHeight;

                if (scaleWidth > scaleHeight) {
                        w = (int) (imageWidth * scaleHeight);
                        h = (int) (imageHeight * scaleHeight);
                } else {
                        w = (int) (imageWidth * scaleWidth);
                        h = (int) (imageHeight * scaleWidth);
                }

                BufferedImage scaled = new BufferedImage(w, h, image.getType());
                java.awt.Graphics2D g2 = scaled.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.drawImage(image, 0, 0, w, h, null);
                g2.dispose();

                return scaled;
        }
}