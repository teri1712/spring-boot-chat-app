package com.decade.practice.authentication;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.core.UserOperations;
import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.model.domain.entity.EventTypes;
import com.decade.practice.model.local.AccountEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("development")
@ContextConfiguration(classes = {DevelopmentApplication.class})
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTest {

      @LocalServerPort
      private int port = 0;

      @Autowired
      private UserOperations userOperations;

      @Autowired
      private PasswordEncoder passwordEncoder;

      @Autowired
      private UserRepository userRepository;


      private RestClient client;

      @BeforeAll
      public void setUp() {
            userOperations.create("abc", "abc", "abc", null, "male", null, true);
      }

      @Test
      public void given_validCredentials_when_login_then_returnsAccountWithChatData() throws Exception {
            client = RestClient.builder()
                  .baseUrl("http://localhost:" + port + "/login")
                  .build();

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("username", "abc");
            form.add("password", "abc");

            AccountEntry received = client.post()
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                  .body(form)
                  .retrieve()
                  .body(AccountEntry.class);

            assertNotNull(received);
            assertNotNull(received.getAccount());
            assertNotNull(received.getChatSnapshots());
            assertNotNull(received.getChatSnapshots().size() == 1);
            assertNotNull(received.getChatSnapshots().get(0).getEventList().size() == 1);
            assertNotNull(received.getChatSnapshots().get(0).getEventList().get(0).getEdges().size() == 1);
            assertNotNull(received.getChatSnapshots().get(0).getEventList().get(0).getEventType().equals(EventTypes.WELCOME));
      }

      @Test
      public void given_invalidCredentials_when_login_then_throwsUnauthorized() throws Exception {
            client = RestClient.builder()
                  .baseUrl("http://localhost:" + port + "/login")
                  .build();

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("username", "abc");
            form.add("password", "zzz");

            assertThrows(HttpClientErrorException.Unauthorized.class, () -> {
                  client.post()
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(form)
                        .retrieve()
                        .body(AccountEntry.class);
            });
      }
}