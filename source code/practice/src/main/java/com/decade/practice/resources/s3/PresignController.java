package com.decade.practice.resources.s3;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class PresignController {

      private final PresignedUrlService service;

      @PostMapping("/upload-urls")
      public S3PresignedResponse uploadUrl(@RequestParam String filename) {
            String user = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication().getName();
            return service.generateUploadUrl(filename, user);
      }
}
