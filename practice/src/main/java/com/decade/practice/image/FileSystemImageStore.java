package com.decade.practice.image;

import com.decade.practice.model.domain.embeddable.ImageSpec;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

import static com.decade.practice.image.LocalImageConfiguration.*;

public class FileSystemImageStore implements ImageStore {

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