package com.decade.practice.image;

import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.utils.CacheUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.UUID;

@Configuration
public class LocalImageConfiguration {

      static final String FILE_SYSTEM_IMAGE_STORE = "fileSystemImageStore";
      static final String DIRECTORY = "./images/";
      static final String FORMAT = "jpeg";
      static final String PATH = "/image";
      static final String QUERY = "filename=";
      static final String QUERY_PATH = PATH + "?" + QUERY;

      @Bean(FILE_SYSTEM_IMAGE_STORE)
      public ImageStore fileSystemImageStore() {
            return new FileSystemImageStore();
      }

      @Configuration
      @RestController
      @RequestMapping(PATH)
      public static class ImageController {
            private final ImageStore store;

            public ImageController(@Qualifier(FILE_SYSTEM_IMAGE_STORE) ImageStore store) {
                  this.store = store;
            }

            @GetMapping
            public ResponseEntity<Resource> get(HttpServletRequest request) {
                  try {
                        String requestURL = request.getRequestURL().toString();
                        String queryString = request.getQueryString();
                        URI uri = new URL(requestURL + "?" + queryString).toURI();
                        Resource resource = store.read(uri);
                        var cacheControl = CacheUtils.DEFAULT_CACHE_CONTROL;

                        return ResponseEntity.ok()
                              .cacheControl(cacheControl)
                              .contentType(MediaType.IMAGE_JPEG)
                              .body(resource);
                  } catch (Exception e) {
                        return ResponseEntity.notFound().build();
                  }
            }
      }

      static class FileSystemImageStore implements ImageStore {

            private String getFilename(URL url) {
                  return url.getQuery().substring(QUERY.length());
            }

            @Override
            public boolean support(URI uri) {
                  String scheme = uri.getScheme();

                  if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                        try {
                              return uri.toURL().getPath().startsWith(PATH);
                        } catch (IOException e) {
                              return false;
                        }
                  }
                  return false;
            }

            @Override
            public ImageSpec save(BufferedImage image) throws IOException {
                  HttpServletRequest httpRequest =
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                  File directory = new File(DIRECTORY);
                  if (!directory.exists()) {
                        directory.mkdir();
                  }

                  String filename = UUID.randomUUID().toString() + "." + FORMAT;
                  File file = new File(directory, filename);
                  ImageIO.write(image, FORMAT, file);

                  String scheme = httpRequest.getScheme();
                  String server = httpRequest.getServerName();
                  int port = httpRequest.getServerPort();

                  String base = scheme + "://" + server + ":" + port;
                  String uri = new URL(base + QUERY_PATH + filename).toString();
                  return new ImageSpec(uri, filename, image.getWidth(), image.getHeight(), FORMAT);
            }

            @Override
            public Resource read(URI uri) throws IOException {
                  String filename = getFilename(uri.toURL());
                  java.nio.file.Path filePath = Paths.get(DIRECTORY).resolve(filename);
                  Resource resource = new UrlResource(filePath.toUri());
                  if (resource.exists() || resource.isReadable()) {
                        return resource;
                  }
                  throw new FileNotFoundException("File not found: " + filename);
            }

            @Override
            public void remove(URI uri) throws IOException {
                  String filename = getFilename(uri.toURL());
                  File file = new File(DIRECTORY + filename);
                  if (!file.isFile() || !file.delete()) {
                        throw new FileNotFoundException("File not found: " + filename);
                  }
            }
      }
}