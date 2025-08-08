package com.decade.practice.authentication;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.entities.domain.DefaultAvatar;
import com.decade.practice.entities.domain.entity.User;
import com.decade.practice.entities.dto.SignUpRequest;
import com.decade.practice.medias.ImageStore;
import com.decade.practice.security.jwt.JwtCredentialService;
import com.decade.practice.usecases.core.UserOperations;
import com.decade.practice.web.advices.ExceptionControllerAdvice;
import com.decade.practice.web.rest.TokenController;
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
import java.util.Base64;

import static com.decade.practice.entities.dto.SignUpRequest.MAX_USERNAME_LENGTH;
import static com.decade.practice.entities.dto.SignUpRequest.MIN_USERNAME_LENGTH;

@WebMvcTest(controllers = TokenController.class)
@ActiveProfiles("development")
@ContextConfiguration(classes = DevelopmentApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(OutputCaptureExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(ExceptionControllerAdvice.class)
public class SignUpValidationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserOperations userOperations;

        @MockBean
        private PasswordEncoder encoder;

        @MockBean
        private JwtCredentialService credentialService;

        @MockBean
        private SecurityContextRepository securityContextRepository;

        @MockBean
        private ImageStore imageStore;

        private static final String ONE_PIXEL_BMP_BASE64 =
                "Qk06AAAAAAAAADYAAAAoAAAAAQAAAAEAAAABABgAAAAAAAQAAAATCwAAEwsAAAAAAAAAAAD///8A";

        private static final byte[] ONE_PIXEL_BMP_BYTES = Base64.getDecoder().decode(ONE_PIXEL_BMP_BASE64);

        @BeforeEach
        public void setUp() throws IOException {
                Mockito.when(encoder.encode(Mockito.anyString())).thenAnswer(invocation ->
                        invocation.getArgument(0, String.class));

                Mockito.when(userOperations.create(
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
        public void given_usernameWithSpaces_when_signUp_then_returnsValidationError() throws Exception {
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
                                MockMvcRequestBuilders.multipart("/authentication/sign-up")
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
        public void given_usernameTooShort_when_signUp_then_returnsLengthValidationError() throws Exception {
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
                                MockMvcRequestBuilders.multipart("/authentication/sign-up")
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
        public void given_weakPassword_when_signUp_then_returnsPasswordValidationError() throws Exception {
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
                                MockMvcRequestBuilders.multipart("/authentication/sign-up")
                                        .file(new MockMultipartFile("file", "filename.txt", "text/plain", "file content".getBytes()))
                                        .file(informationPart)
                        )
                        .andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Password too weak")));
        }

        @Test
        public void given_validSignupData_when_signUp_then_succeeds() throws Exception {
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
                                MockMvcRequestBuilders.multipart("/authentication/sign-up")
                                        .file(new MockMultipartFile("file", "avatar.bmp", "image/bmp", ONE_PIXEL_BMP_BYTES))
                                        .file(informationPart)
                        )
                        .andExpect(MockMvcResultMatchers.status().isCreated());
        }
}
