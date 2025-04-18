package com.decade.practice.image;

import com.decade.practice.model.embeddable.ImageSpec;
import org.springframework.core.io.Resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

public interface ImageStore {

      boolean support(URI uri);

      ImageSpec save(BufferedImage image) throws IOException;

      Resource read(URI uri) throws IOException;

      void remove(URI uri) throws IOException;
}