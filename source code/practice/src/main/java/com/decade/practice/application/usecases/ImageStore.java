package com.decade.practice.application.usecases;

import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import org.springframework.core.io.Resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

public interface ImageStore {

    ImageSpec save(BufferedImage image) throws IOException;

    Resource read(URI uri) throws IOException;

    void remove(URI uri) throws IOException;
}