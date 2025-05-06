package com.decade.practice;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
      webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
      properties = {
            "spring.jpa.database=H2"})
@AutoConfigureTestDatabase(
      connection = EmbeddedDatabaseConnection.H2
) // or using autoconfigured embedded datasource.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BootstrapTest {

      @Test
      public void given_application_when_contextLoads_then_startsSuccessfully() {
      }
}
