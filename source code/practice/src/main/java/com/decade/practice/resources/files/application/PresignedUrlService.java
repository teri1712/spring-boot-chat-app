package com.decade.practice.resources.files.application;

import com.decade.practice.resources.files.application.ports.out.StoragePathGenerator;
import com.decade.practice.resources.files.dto.S3PresignedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresignedUrlService {

      private final StoragePathGenerator pathGenerator;

      public S3PresignedResponse generateUploadUrl(String filename, String username) {

            StoragePathGenerator.Presigned generation = pathGenerator.generatePresignUpload(username, filename);
            String key = generation.key();
            return S3PresignedResponse.builder()
                      .fileKey(key)
                      .presignedUploadUrl(generation.url())
                      .filename(filename)
                      .build();
      }

}
