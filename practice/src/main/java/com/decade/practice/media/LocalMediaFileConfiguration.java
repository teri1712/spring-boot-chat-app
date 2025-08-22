package com.decade.practice.media;

import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.utils.CacheUtils;
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

/**
 * Spring configuration for local file-based media storage.
 * This configuration is activated only when no other {@link MediaStore} bean is defined in the context.
 * It provides beans for storing, retrieving, and serving media files and images from the local filesystem.
 */
@Configuration
@ConditionalOnMissingBean(MediaStore.class)
public class LocalMediaFileConfiguration {


        /**
         * Extracts a filename from a query string.
         * Note: This is a simplistic implementation that assumes the query string is in the format "filename=...".
         *
         * @param query The query string from the request.
         * @return A {@link Path} to the file within the "medias" directory.
         */
        private static Path extractFile(String query) {
                // A more robust implementation would use @RequestParam in the controller.
                String filename = query.substring("filename=".length());
                return Paths.get("./medias/").resolve(filename);
        }

        /**
         * A REST controller for serving media files (images and generic files) from the local filesystem.
         * It's defined as a nested configuration to keep it co-located with the local storage logic.
         */
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
                                var cacheControl = CacheUtils.ONE_MONTHS;

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
                                var cacheControl = CacheUtils.ONE_MONTHS;

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

        @Component
        public static class FileSystemImageStore implements ImageStore {
                private final MediaStore mediaStore;

                public FileSystemImageStore(MediaStore mediaStore) {
                        this.mediaStore = mediaStore;
                }

                @Override
                public ImageSpec save(BufferedImage image) throws IOException {
                        HttpServletRequest httpRequest =
                                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                        String scheme = httpRequest.getScheme();
                        String server = httpRequest.getServerName();
                        int port = httpRequest.getServerPort();
                        String filename = UUID.randomUUID() + "." + ImageSpec.DEFAULT_FORMAT;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, ImageSpec.DEFAULT_FORMAT, baos);
                        baos.flush();
                        byte[] imageBytes = baos.toByteArray();
                        baos.close();

                        ByteArrayResource resource = new ByteArrayResource(imageBytes);
                        mediaStore.save(resource, filename);
                        String base = scheme + "://" + server + ":" + port;
                        String uri = new URL(base + "medias?filename=" + filename).toString();
                        return new ImageSpec(uri, filename, image.getWidth(), image.getHeight(), ImageSpec.DEFAULT_FORMAT);
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