package com.decade.practice.web;

import com.decade.practice.DevelopmentApplication;
import com.decade.practice.MockEndpoints;
import com.decade.practice.adapter.security.DaoUserDetailsService;
import com.decade.practice.adapter.security.jwt.JwtService;
import com.decade.practice.adapter.security.jwt.JwtTokenFilter;
import com.decade.practice.adapter.security.strategies.LoginSuccessStrategy;
import com.decade.practice.adapter.security.strategies.LogoutStrategy;
import com.decade.practice.adapter.security.strategies.Oauth2LoginSuccessStrategy;
import com.decade.practice.application.usecases.ConversationRepository;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.locals.Account;
import com.decade.practice.domain.repositories.AdminRepository;
import com.decade.practice.domain.repositories.UserRepository;
import com.decade.practice.infra.configs.SecurityConfiguration;
import com.decade.practice.infra.configs.SessionConfiguration;
import com.decade.practice.utils.DummyRedisSetOps;
import com.decade.practice.utils.PrerequisiteBeans;
import com.decade.practice.utils.RedisTestContainerSupport;
import com.decade.practice.utils.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = MockEndpoints.class)
@ActiveProfiles("development")
@ContextConfiguration(classes = {DevelopmentApplication.class, PrerequisiteBeans.class})
@ExtendWith(OutputCaptureExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({
        SecurityConfiguration.class,
        SessionConfiguration.class,
        LoginSuccessStrategy.class,
        JwtTokenFilter.class,
        MockEndpoints.class,
        RedisAutoConfiguration.class,
        Oauth2LoginSuccessStrategy.class,
        SessionAutoConfiguration.class,
        JwtService.class,
        DaoUserDetailsService.class,
})
public class SecurityFilterTest extends RedisTestContainerSupport {

        private static final String USERNAME = "mock_username";
        private static final String PASSWORD = "mock_password";

        @MockBean
        private UserRepository userRepo;

        @MockBean
        private AdminRepository adminRepository;

        @MockBean
        private ConversationRepository conversationRepository;

        @MockBean
        private UserService userService;

        @MockBean
        private LogoutStrategy logoutHandler;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private PasswordEncoder encoder;

        @MockBean
        private StringRedisTemplate redisTemplate;

        @Autowired
        private JwtService credentialService;

        @BeforeEach
        public void setUp() {
                User user = new User(USERNAME, encoder.encode(PASSWORD));
                Mockito.when(userRepo.findByUsername(USERNAME)).thenReturn(user);
                Mockito.when(userService.prepareAccount(Mockito.any(UserDetails.class))).thenReturn(new Account(user, null));
                Mockito.when(redisTemplate.opsForSet()).thenReturn(new DummyRedisSetOps<String, String>());
        }

        @Test
        public void testLoginWithNonExistentUsernameFails() throws Exception {
                mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin().user("vcl").password(PASSWORD))
                        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                        .andExpect(MockMvcResultMatchers.content().string("Username not found"));
        }

        @Test
        public void testLoginWithValidCredentialsSucceeds() throws Exception {
                mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin().user(USERNAME).password(PASSWORD))
                        .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        public void testLoginWithInvalidPasswordFails() throws Exception {
                mockMvc.perform(
                                SecurityMockMvcRequestBuilders.formLogin()
                                        .user(USERNAME)
                                        .password("vcl")
                        )
                        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                        .andExpect(MockMvcResultMatchers.content().string("Wrong password"));
        }

        @Test
        public void testAccessProtectedResourceWithValidTokenSucceeds() throws Exception {
                User user = userRepo.findByUsername(USERNAME);
                String accessToken = credentialService.create(user, null).getAccessToken();

                mockMvc.perform(
                                MockMvcRequestBuilders.get("/mock")
                                        .header(TokenUtils.HEADER_NAME, TokenUtils.BEARER + " " + accessToken)
                        )
                        .andExpect(MockMvcResultMatchers.status().isOk());
        }
}