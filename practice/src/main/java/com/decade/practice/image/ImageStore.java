package com.decade.practice.image;

import org.springframework.core.io.Resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

public interface ImageStore {

      boolean support(URI uri);

      URI save(BufferedImage image) throws IOException;

      Resource read(URI uri) throws IOException;

      void remove(URI uri) throws IOException;
}