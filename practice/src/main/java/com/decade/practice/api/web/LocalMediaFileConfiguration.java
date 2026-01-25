package com.decade.practice.api.web;

import com.decade.practice.application.usecases.ImageStore;
import com.decade.practice.application.usecases.MediaStore;
import com.decade.practice.persistence.jpa.embeddables.ImageSpec;
import com.decade.practice.utils.WebCacheUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Spring configuration for local file-based media storage.
 * This configuration is activated only when no other {@link MediaStore} bean is defined in the context.
 * It provides beans for storing, retrieving, and serving media files and images from the local filesystem.
 */
// TODO: Migrate to S3 and presign
@Configuration
@ConditionalOnMissingBean(MediaStore.class)
public class LocalMediaFileConfiguration {


    /**
     * A REST controller for serving media files (images and generic files) from the local filesystem.
     * It's defined as a nested configuration to keep it co-located with the local storage logic.
     */
    @RestController
    @RequestMapping("/medias")
    //TODO: migrate to S3
    public static class MediaController {
        private final MediaStore mediaStore;

        public MediaController(MediaStore mediaStore) {
            this.mediaStore = mediaStore;
        }

        @GetMapping("/{filename}")
        public ResponseEntity<Resource> getFile(@PathVariable String filename) {
            try {
                Resource resource = mediaStore.read(Paths.get("./medias/").resolve(filename).toUri());
                var cacheControl = WebCacheUtils.ONE_MONTHS;
                var mediaType = org.springframework.http.MediaTypeFactory.getMediaType(filename)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM);
                return ResponseEntity.ok()
                        .cacheControl(cacheControl)
                        .contentType(mediaType)
                        .body(resource);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.notFound().build();
            }
        }
    }

    @Component
    public static class FileSystemMediaStore implements MediaStore {
        private final File fileDirectory;

        public FileSystemMediaStore() {
            this.fileDirectory = new File("./medias/");
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
            HttpServletRequest httpRequest =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String scheme = httpRequest.getScheme();
            String server = httpRequest.getServerName();
            int port = httpRequest.getServerPort();
            String base = scheme + "://" + server + ":" + port;

            File file = new File(fileDirectory, name);
            Files.copy(resource.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            try {
                return new URL(base + "/medias/" + name).toURI();
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
        }

        @Override
        public void remove(URI uri) throws IOException {
            File file = new File("." + uri.getPath());
            if (!file.isFile() || !file.delete()) {
                throw new FileNotFoundException("File not found: " + uri.getPath());
            }
        }
    }

    @Component
    public static class FileSystemImageStore implements ImageStore {
        private final MediaStore mediaStore;

        public FileSystemImageStore(MediaStore mediaStore) {
            this.mediaStore = mediaStore;
        }

        @Override
        public ImageSpec save(BufferedImage image) throws IOException {

            // Convert to JPG format
            BufferedImage jpgImage = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            jpgImage.createGraphics().drawImage(image, 0, 0, null);

            String filename = UUID.randomUUID() + ".jpg";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(jpgImage, "jpg", baos);
            baos.flush();
            baos.close();
            byte[] imageBytes = baos.toByteArray();

            ByteArrayResource resource = new ByteArrayResource(imageBytes);
            URI uri = mediaStore.save(resource, filename);
            return new ImageSpec(uri.toString(), filename, jpgImage.getWidth(), jpgImage.getHeight(), "jpg");
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