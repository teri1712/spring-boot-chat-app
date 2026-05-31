package com.decade.practice.resources.files.adapter;

import com.decade.practice.resources.files.application.PresignedUrlService;
import com.decade.practice.resources.files.dto.S3PresignedResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class PresignController {

      private final PresignedUrlService service;

      @PostMapping("/upload")
      public S3PresignedResponse uploadUrl(@RequestParam String filename) {
            String user = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication().getName();
            return service.generateUploadUrl(filename, user);
      }

}
