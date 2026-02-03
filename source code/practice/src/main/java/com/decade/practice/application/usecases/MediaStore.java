package com.decade.practice.application.usecases;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URI;

public interface MediaStore {

        Resource read(URI uri) throws IOException;

        URI save(Resource resource, String name) throws IOException;

        void remove(URI uri) throws IOException;
}
