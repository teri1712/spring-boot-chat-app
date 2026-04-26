package com.decade.practice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.RecordApplicationEvents;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;

@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RecordApplicationEvents
@SpringBootTest
@Import({TestBeans.class, ContainerConfigs.class})
public abstract class BaseTestClass {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void cleanUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Autowired
    S3Client s3Client;

    @Value("${aws.s3.bucket}")
    String bucket;

    @BeforeEach
    void setUpBucket() {

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
