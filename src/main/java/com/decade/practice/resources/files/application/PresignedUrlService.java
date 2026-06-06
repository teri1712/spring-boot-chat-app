package com.decade.practice.resources.files.application;

import com.decade.practice.resources.files.application.ports.out.StoragePathGenerator;
import com.decade.practice.resources.files.dto.PresignedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresignedUrlService {

      private final StoragePathGenerator pathGenerator;

      public PresignedResponse generateUploadUrl(String filename, String username) {

            StoragePathGenerator.Presigned generation = pathGenerator.generatePresignUpload(username, filename);
            String key = generation.key();
            return PresignedResponse.builder()
                      .fileKey(key)
                      .presignedUploadUrl(generation.url())
                      .filename(filename)
                      .build();
      }

}
