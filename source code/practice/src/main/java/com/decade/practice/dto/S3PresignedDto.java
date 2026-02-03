package com.decade.practice.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class S3PresignedDto {
    private String key;
    private String bucket;
    private String filename;
    private String presignedUploadUrl;
    private String downloadUrl;
}
