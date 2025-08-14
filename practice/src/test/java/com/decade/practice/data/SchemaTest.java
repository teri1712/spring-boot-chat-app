package com.decade.practice.data;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.data.database.DatabaseConfiguration;
import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.security.TokenCredentialService;
import com.decade.practice.usecases.EventStore;
import com.decade.practice.usecases.UserOperations;
import com.decade.practice.usecases.UserService;
import com.decade.practice.utils.PrerequisiteBeans;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@DataJpaTest
@ActiveProfiles("development")
@ContextConfiguration(classes = {DevelopmentApplication.class, PrerequisiteBeans.class})
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
        private EventStore eventStore;

        @MockBean
        private TokenCredentialService credentialService;

        @Test
        public void testInsert() {
                userOperations.create("first", "first", "first", new Date(), "male", null, true);
        }
}
