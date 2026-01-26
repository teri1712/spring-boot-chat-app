package com.decade.practice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class S3PresignedDto {
    private String key;
    private String bucket;
    private String filename;
    private String presignedUploadUrl;
    private String downloadUrl;
}
