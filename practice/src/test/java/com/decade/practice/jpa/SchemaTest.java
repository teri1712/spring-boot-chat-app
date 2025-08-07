package com.decade.practice.jpa;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.database.DatabaseConfiguration;
import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.usecases.UserService;
import com.decade.practice.usecases.core.TokenCredentialService;
import com.decade.practice.usecases.core.UserOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@DataJpaTest
@ActiveProfiles("development")
@ContextConfiguration(classes = DevelopmentApplication.class)
@ExtendWith(OutputCaptureExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({UserService.class, DatabaseConfiguration.class})
public class SchemaTest {

      @Autowired
      private TestEntityManager entityManager;

      @Autowired
      private UserRepository userRepo;

      @Autowired
      private UserOperations userOperations;

      @MockBean
      private PasswordEncoder encoder;

      @MockBean
      private TokenCredentialService credentialService;

      @BeforeEach
      public void setUp() {
            Mockito.when(encoder.encode(Mockito.anyString())).thenAnswer(invocation ->
                  invocation.getArgument(0, String.class)
            );
            // userRepo.saveAndFlush(mockUser());
      }

      @Test
      public void testInsert() {
            userOperations.create("first", "first", "first", new Date(), "male", null, true);
      }
}
