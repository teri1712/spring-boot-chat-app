package com.decade.practice.image;

import com.decade.practice.utils.CacheUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URL;

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
}