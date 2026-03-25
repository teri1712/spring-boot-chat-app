package com.decade.practice.resources.s3;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class S3PresignedResponse {
    private String key;
    private String bucket;
    private String filename;
    private String presignedUploadUrl;
    private String downloadUrl;
}
