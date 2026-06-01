package com.decade.practice.resources.files.adapter;

import com.decade.practice.resources.files.api.FileIntegrity;
import com.decade.practice.resources.files.api.FileIntegrityException;
import com.decade.practice.resources.files.application.ports.out.StoragePathGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3StorageGenerator implements StoragePathGenerator {

      private final S3Presigner presigner;
      private final S3Client s3Client;

      @Value("${aws.s3.bucket}")
      private String bucket;

      @Value("${aws.s3.endpoint}")
      private String s3Endpoint;

      private String generateKey(String username, String filename) {
            return username + "/" + filename;
      }

      @Override
      public Presigned generatePresignUpload(String username, String filename) {
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

            String url = presigner.presignPutObject(presignRequest)
                      .url()
                      .toString();
            return new Presigned(key, url);
      }

      @Override
      public String generateDownload(FileIntegrity file) {
            String fileKey = file.fileKey();
            String eTag = file.eTag();
            String expectedEtag = getEtag(fileKey);
            if (eTag == null || !eTag.equals(expectedEtag)) {
                  throw new FileIntegrityException(fileKey, bucket, expectedEtag, eTag);
            }
            String[] parts = fileKey.split("/");
            String username = parts[0];
            String filename = parts[1];
            return s3Endpoint + "/" + bucket + "/" + UriUtils.encodePath(username, StandardCharsets.UTF_8) + "/" + UriUtils.encodePath(filename, StandardCharsets.UTF_8);
      }

      private String getEtag(String key) {
            HeadObjectResponse res = s3Client.headObject(
                      HeadObjectRequest.builder()
                                .bucket(bucket)
                                .key(key)
                                .build()
            );
            return res.eTag();
      }
}
