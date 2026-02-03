package com.decade.practice.api.web.s3;

import com.decade.practice.dto.S3PresignedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PresignedUrlService {

    private final S3Presigner presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.endpoint}")
    private String s3Endpoint;

    public String generateKey(String username, String filename) {
        return username + "/" + filename;
    }

    public String getDownloadUrl(String username, String filename) {
        return s3Endpoint + "/" + bucket + "/" + UriUtils.encodePath(username, StandardCharsets.UTF_8) + "/" + UriUtils.encodePath(filename, StandardCharsets.UTF_8);
    }

    public S3PresignedDto generateUploadUrl(String filename, String username) {
        String key = generateKey(username, filename);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/octet-stream")
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .putObjectRequest(putObjectRequest)
                        .build();

        String uploadUrl = presigner.presignPutObject(presignRequest)
                .url()
                .toString();
        return S3PresignedDto.builder()
                .key(key)
                .bucket(bucket)
                .presignedUploadUrl(uploadUrl)
                .filename(filename)
                .downloadUrl(getDownloadUrl(username, filename))
                .build();
    }

}
