package com.decade.practice.resources.files.api;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileIntegrityException extends RuntimeException {
      private String key;
      private String bucket;
      private String expectedEtag;
      private String actualETag;

      @Override
      public String getMessage() {
            return "File Integrity Exception: " + key + " in bucket expected Etag: " + expectedEtag + " but got: " + actualETag;
      }
}
