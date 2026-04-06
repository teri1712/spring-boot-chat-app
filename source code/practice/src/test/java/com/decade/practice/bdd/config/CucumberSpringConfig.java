package com.decade.practice.bdd.config;

import com.decade.practice.BaseTestClass;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CucumberSpringConfig extends BaseTestClass {
      @Autowired
      private S3Client s3Client;
      private @Value("${aws.s3.bucket}") String bucket;

      @Before
      public void setUpBucket() {
            try {
                  s3Client.createBucket(CreateBucketRequest.builder()
                            .bucket(bucket)
                            .build());
            } catch (Exception ex) {
                  log.debug("Bucket already exists", ex);
            }
      }
}
