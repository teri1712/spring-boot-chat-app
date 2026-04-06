package com.decade.practice.resources.files.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class S3PresignedResponse {
      private String fileKey;
      private String filename;
      private String presignedUploadUrl;
}
