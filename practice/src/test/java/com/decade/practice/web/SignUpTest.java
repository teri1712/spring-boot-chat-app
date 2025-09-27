package com.decade.practice.web;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.adapter.security.jwt.JwtService;
import com.decade.practice.adapter.web.advices.ExceptionControllerAdvice;
import com.decade.practice.adapter.web.rest.TokenController;
import com.decade.practice.adapter.web.rest.UserController;
import com.decade.practice.application.dto.SignUpRequest;
import com.decade.practice.application.usecases.ConversationRepository;
import com.decade.practice.application.usecases.ImageStore;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.domain.DefaultAvatar;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.UserRepository;
import com.decade.practice.utils.PrerequisiteBeans;
import com.decade.practice.utils.RedisTestContainerSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.decade.practice.application.dto.SignUpRequest.MAX_USERNAME_LENGTH;
import static com.decade.practice.application.dto.SignUpRequest.MIN_USERNAME_LENGTH;
import static com.decade.practice.utils.Media.ONE_PIXEL_BMP_BYTES;

@WebMvcTest(controllers = {TokenController.class, UserController.class})
@ActiveProfiles("development")
@ContextConfiguration(classes = {DevelopmentApplication.class, PrerequisiteBeans.class})
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(OutputCaptureExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(ExceptionControllerAdvice.class)
public class SignUpTest extends RedisTestContainerSupport {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private UserRepository userRepo;

        @MockBean
        private PasswordEncoder encoder;

        @MockBean
        private JwtService credentialService;

        @MockBean
        private SecurityContextRepository securityContextRepository;

        @MockBean
        private ImageStore imageStore;

        @MockBean
        private ConversationRepository conversationRepository;


        @BeforeEach
        public void setUp() throws IOException {
                Mockito.when(encoder.encode(Mockito.anyString())).thenAnswer(invocation ->
                        invocation.getArgument(0, String.class));

                Mockito.when(userService.create(
                        Mockito.anyString(),      // username
                        Mockito.anyString(),      // password
                        Mockito.anyString(),      // name
                        Mockito.any(),            // dob
                        Mockito.anyString(),      // gender
                        Mockito.any(),            // avatar
                        Mockito.anyBoolean()
                )).thenReturn(new User("123", "123"));

                Mockito.when(imageStore.save(Mockito.any())).thenReturn(DefaultAvatar.getInstance());
        }

        @Test
        public void testSignUpWithUsernameWithSpacesReturnsValidationError() throws Exception {
                SignUpRequest dto = new SignUpRequest(
                        "user name",
                        "password",
                        "user",
                        "Male",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2021-07-20")
                );

                MockMultipartFile informationPart = new MockMultipartFile(
                        "information",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        new ObjectMapper().writeValueAsBytes(dto)
                );

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/users")
                                        .file(new MockMultipartFile("file", "filename.txt", "text/plain", "file content".getBytes()))
                                        .file(informationPart)
                        )
                        .andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andExpect(
                                MockMvcResultMatchers.content()
                                        .string(Matchers.containsString("Username must not contain spaces."))
                        );
        }

        @Test
        public void testSignUpWithUsernameTooShortReturnsLengthValidationError() throws Exception {
                SignUpRequest dto = new SignUpRequest(
                        "user",
                        "password",
                        "user",
                        "Male",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2021-07-20")
                );

                MockMultipartFile informationPart = new MockMultipartFile(
                        "information",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        new ObjectMapper().writeValueAsBytes(dto)
                );

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/users")
                                        .file(new MockMultipartFile("file", "filename.txt", "text/plain", "file content".getBytes()))
                                        .file(informationPart)
                        )
                        .andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andExpect(
                                MockMvcResultMatchers.content()
                                        .string(Matchers.containsString("Username length must be between " + MIN_USERNAME_LENGTH + " and " + MAX_USERNAME_LENGTH + " characters"))
                        );
        }

        @Test
        public void testSignUpWithWeakPasswordReturnsPasswordValidationError() throws Exception {
                SignUpRequest dto = new SignUpRequest(
                        "username",
                        "pass",
                        "user",
                        "Male",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2021-07-20")
                );

                MockMultipartFile informationPart = new MockMultipartFile(
                        "information",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        new ObjectMapper().writeValueAsBytes(dto)
                );

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/users")
                                        .file(new MockMultipartFile("file", "filename.txt", "text/plain", "file content".getBytes()))
                                        .file(informationPart)
                        )
                        .andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Password too weak")));
        }

        @Test
        public void testSignUpWithValidDataSucceeds() throws Exception {
                SignUpRequest dto = new SignUpRequest(
                        "username",
                        "password",
                        "user",
                        "Male",
                        new SimpleDateFormat("yyyy-MM-dd").parse("2021-07-20")
                );

                MockMultipartFile informationPart = new MockMultipartFile(
                        "information",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        new ObjectMapper().writeValueAsBytes(dto)
                );

                mockMvc.perform(
                                MockMvcRequestBuilders.multipart("/users")
                                        .file(new MockMultipartFile("file", "avatar.bmp", "image/bmp", ONE_PIXEL_BMP_BYTES))
                                        .file(informationPart)
                        )
                        .andExpect(MockMvcResultMatchers.status().isCreated());
        }
}