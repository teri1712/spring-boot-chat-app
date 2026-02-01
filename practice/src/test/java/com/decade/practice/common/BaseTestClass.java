package com.decade.practice.common;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseTestClass {

    static PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("chatapp")
                    .withUsername("test")
                    .withPassword("test");

    static GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7")
                    .withExposedPorts(6379);

    static LocalStackContainer LOCALSTACK =
            new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack:latest")
            )
                    .withServices(LocalStackContainer.Service.S3);

    static ElasticsearchContainer ELASTICSEARCH =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.12.2")
                    .withEnv("xpack.security.enabled", "false")
                    .withEnv("discovery.type", "single-node");


    static KafkaContainer KAFKA =
            new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"));

    static {
        POSTGRES.start();
        REDIS.start();
        LOCALSTACK.start();
        ELASTICSEARCH.start();
        KAFKA.start();

    }

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));

        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        registry.add("aws.s3.endpoint", () -> LOCALSTACK.getEndpointOverride(S3).toString());
        registry.add("aws.s3.access.id", () -> LOCALSTACK.getAccessKey());
        registry.add("aws.s3.access.secret", () -> LOCALSTACK.getSecretKey());
        registry.add("aws.s3.region", () -> LOCALSTACK.getRegion());

        registry.add("spring.elasticsearch.uris", ELASTICSEARCH::getHttpHostAddress);


        registry.add("spring.kafka.bootstrap-servers",
                KAFKA::getBootstrapServers);
    }


    @AfterAll
    static void cleanUp(@Autowired RedisTemplate<String, Object> redisTemplate) {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @BeforeAll
    static void setUpBucket(@Value("${aws.s3.bucket}") String bucket) {

        try (S3Client s3Client = S3Client.builder()
                .endpointOverride(LOCALSTACK.getEndpointOverride(S3))
                .region(Region.of(LOCALSTACK.getRegion()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(LOCALSTACK.getAccessKey(), LOCALSTACK.getSecretKey())
                ))
                .build()) {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucket)
                    .build());
        }
    }


}
