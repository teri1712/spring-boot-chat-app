package com.decade.practice.media;

import com.decade.practice.entities.domain.embeddable.ImageSpec;
import com.decade.practice.utils.CacheUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Configuration
@ConditionalOnMissingBean(MediaStore.class)
public class LocalMediaFileConfiguration {

      private static final String FORMAT = "jpeg";

      private static Path extractFile(String query) {
            String filename = query.substring("filename=".length());
            return Paths.get("./medias/").resolve(filename);
      }

      @Configuration
      @RestController
      @RequestMapping("/medias")
      public static class MediaController {
            private final MediaStore mediaStore;

            public MediaController(MediaStore mediaStore) {
                  this.mediaStore = mediaStore;
            }

            @GetMapping("/images")
            public ResponseEntity<Resource> getImage(HttpServletRequest request) {
                  try {
                        String queryString = request.getQueryString();
                        Resource resource = mediaStore.read(extractFile(queryString).toUri());
                        var cacheControl = CacheUtils.CACHE_CONTROL;

                        return ResponseEntity.ok()
                              .cacheControl(cacheControl)
                              .contentType(MediaType.IMAGE_JPEG)
                              .header("Content-Disposition", "inline; filename=" + resource.getFilename())
                              .body(resource);
                  } catch (Exception e) {
                        return ResponseEntity.notFound().build();
                  }
            }

            @GetMapping("/files")
            public ResponseEntity<Resource> getFile(HttpServletRequest request) {
                  try {
                        String queryString = request.getQueryString();
                        Resource resource = mediaStore.read(extractFile(queryString).toUri());
                        var cacheControl = CacheUtils.CACHE_CONTROL;

                        return ResponseEntity.ok()
                              .cacheControl(cacheControl)
                              .contentType(MediaType.APPLICATION_OCTET_STREAM)
                              .header("Content-Disposition", "attachment; filename=" + resource.getFilename())
                              .body(resource);
                  } catch (Exception e) {
                        return ResponseEntity.notFound().build();
                  }
            }
      }

      @Bean
      public File fileDirectory() {
            return new File("./images/");
      }

      @Service
      static class FileSystemMediaStore implements MediaStore {
            private final File fileDirectory;

            FileSystemMediaStore(File fileDirectory) {
                  this.fileDirectory = fileDirectory;
                  if (!fileDirectory.exists()) {
                        fileDirectory.mkdir();
                  }
            }

            @Override
            public Resource read(URI uri) throws IOException {
                  return new UrlResource(uri);
            }

            @Override
            public URI save(Resource resource, String name) throws IOException {
                  File file = new File(fileDirectory, name);
                  Files.copy(resource.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                  return file.toURI();
            }

            @Override
            public void remove(URI uri) throws IOException {
                  File file = new File(uri.getPath());
                  if (!file.isFile() || !file.delete()) {
                        throw new FileNotFoundException("File not found: " + uri.getPath());
                  }
            }
      }

      @Service
      static class FileSystemImageStore implements ImageStore {
            private final MediaStore mediaStore;

            FileSystemImageStore(MediaStore mediaStore) {
                  this.mediaStore = mediaStore;
            }

            @Override
            public ImageSpec save(BufferedImage image) throws IOException {
                  HttpServletRequest httpRequest =
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                  String scheme = httpRequest.getScheme();
                  String server = httpRequest.getServerName();
                  int port = httpRequest.getServerPort();
                  String filename = UUID.randomUUID() + "." + FORMAT;
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  ImageIO.write(image, FORMAT, baos);
                  baos.flush();
                  byte[] imageBytes = baos.toByteArray();
                  baos.close();

                  ByteArrayResource resource = new ByteArrayResource(imageBytes);
                  mediaStore.save(resource, filename);
                  String base = scheme + "://" + server + ":" + port;
                  String uri = new URL(base + "medias?filename=" + filename).toString();
                  return new ImageSpec(uri, filename, image.getWidth(), image.getHeight(), FORMAT);
            }

            @Override
            public Resource read(URI uri) throws IOException {
                  Resource resource = mediaStore.read(uri);
                  if (resource.exists() || resource.isReadable()) {
                        return resource;
                  }
                  throw new FileNotFoundException("File not found: " + uri.getPath());
            }

            @Override
            public void remove(URI uri) throws IOException {
                  mediaStore.remove(uri);
            }
      }
}