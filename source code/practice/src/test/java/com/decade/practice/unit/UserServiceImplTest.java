package com.decade.practice.unit;

import com.decade.practice.application.services.UserServiceImpl;
import com.decade.practice.dto.AccountResponse;
import com.decade.practice.dto.ProfileRequest;
import com.decade.practice.dto.SignUpRequest;
import com.decade.practice.dto.UserResponse;
import com.decade.practice.dto.events.UserPasswordChangedEvent;
import com.decade.practice.persistence.jpa.entities.SyncContext;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void givenSignUpRequest_whenCreateUser_thenUserIsCreated() {
        TransactionSynchronizationManager.initSynchronization();
        try {
            SignUpRequest request = new SignUpRequest();
            request.setUsername("testuser");
            request.setPassword("password");
            request.setName("Test User");
            request.setGender(User.MALE);
            request.setDob(new Date());

            given(encoder.encode(any())).willReturn("encodedPassword");

            UserResponse result = userService.create(request, false);

            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            verify(userRepo).save(any(User.class));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void givenProfileRequest_whenChangeProfile_thenProfileIsUpdated() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("Old Name");

        ProfileRequest request = new ProfileRequest();
        request.setName("New Name");

        given(userRepo.findById(userId)).willReturn(Optional.of(user));

        UserResponse result = userService.changeProfile(userId, request);

        assertEquals("New Name", result.getName());
    }

    @Test
    void givenValidPasswords_whenChangePassword_thenPasswordIsChanged() {
        TransactionSynchronizationManager.initSynchronization();
        try {
            UUID userId = UUID.randomUUID();
            User user = new User();
            user.setId(userId);
            user.setUsername("testuser");
            user.setPassword("oldEncodedPassword");

            given(userRepo.findByIdWithPessimisticWrite(userId)).willReturn(Optional.of(user));
            given(encoder.matches("oldPassword", "oldEncodedPassword")).willReturn(true);
            given(encoder.encode("newPassword")).willReturn("newEncodedPassword");

            userService.changePassword(userId, "newPassword", "oldPassword");

            verify(eventPublisher).publishEvent(new UserPasswordChangedEvent("testuser"));
            assertEquals("newEncodedPassword", user.getPassword());
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void givenInvalidPassword_whenChangePassword_thenThrowAccessDeniedException() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setPassword("oldEncodedPassword");

        given(userRepo.findByIdWithPessimisticWrite(userId)).willReturn(Optional.of(user));
        given(encoder.matches("wrongPassword", "oldEncodedPassword")).willReturn(false);

        assertThrows(AccessDeniedException.class, () ->
                userService.changePassword(userId, "newPassword", "wrongPassword")
        );
    }

    @Test
    void givenUsername_whenPrepareAccount_thenAccountResponseIsReturned() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setId(UUID.randomUUID());
        SyncContext syncContext = new SyncContext(user);
        user.setSyncContext(syncContext);

        given(userRepo.findByUsername(username)).willReturn(Optional.of(user));

        AccountResponse result = userService.prepareAccount(username);

        assertNotNull(result);
        assertEquals(username, result.getUser().getUsername());
    }
}
