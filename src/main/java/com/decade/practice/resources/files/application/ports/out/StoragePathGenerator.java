package com.decade.practice.resources.files.application.ports.out;

import com.decade.practice.resources.files.api.DownloadPathGenerator;

public interface StoragePathGenerator extends DownloadPathGenerator {

      Presigned generatePresignUpload(String username, String filename);

      record Presigned(String key, String url) {
      }
}
