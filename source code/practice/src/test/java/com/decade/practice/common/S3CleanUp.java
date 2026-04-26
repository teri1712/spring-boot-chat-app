package com.decade.practice.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;

@Slf4j
@RequiredArgsConstructor
@TestComponent
public class S3CleanUp implements DataCleanUp {
    private final S3Client s3Client;
    private @Value("${aws.s3.bucket}") String bucket;

    @Override
    public void clean() {
        try {
            s3Client.deleteBucket(DeleteBucketRequest.builder()
                .bucket(bucket)
                .build());
        } catch (Exception ignore) {
            log.debug("Bucket {} might be already deleted", bucket);
        }
        s3Client.createBucket(CreateBucketRequest.builder()
            .bucket(bucket)
            .build());
    }
}
