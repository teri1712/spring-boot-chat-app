package com.decade.practice.resources.files.adapter;

import com.decade.practice.resources.files.api.FileIntegrityException;
import com.decade.practice.resources.files.application.PresignedUrlService;
import com.decade.practice.resources.files.dto.S3PresignedResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class PresignController {

      @ExceptionHandler(FileIntegrityException.class)
      @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
      public ProblemDetail handleFileIntegrityException(FileIntegrityException ex) {
            log.warn("File integrity violation", ex);
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
            pd.setTitle("File integrity violation");
            return pd;
      }

      private final PresignedUrlService service;

      @PostMapping("/upload")
      public S3PresignedResponse uploadUrl(@RequestParam String filename) {
            String user = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication().getName();
            return service.generateUploadUrl(filename, user);
      }

}
