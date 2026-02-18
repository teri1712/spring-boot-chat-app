package com.decade.practice.users.unit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
//
//    @Mock
//    private UserRepository userRepo;
//
//    @Mock
//    private PasswordEncoder encoder;
//
//    @InjectMocks
//    private UserPasswordPolicy passwordPolicy;
//
//    @Mock
//    private ApplicationEventPublisher eventPublisher;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    @Test
//    void givenSignUpRequest_whenCreateUser_thenUserIsCreated() {
//        TransactionSynchronizationManager.initSynchronization();
//        try {
//            SignUpRequest request = new SignUpRequest();
//            request.setUsername("testuser");
//            request.setPassword("password");
//            request.setName("Test User");
//            request.setGender(User.MALE);
//            request.setDob(new Date());
//
//            given(encoder.encode(any())).willReturn("encodedPassword");
//
//            UserResponse result = userService.create(request, false);
//
//            assertNotNull(result);
//            assertEquals("testuser", result.username());
//            verify(userRepo).save(any(User.class));
//        } finally {
//            TransactionSynchronizationManager.clearSynchronization();
//        }
//    }
//
//    @Test
//    void givenProfileRequest_whenChangeProfile_thenProfileIsUpdated() {
//        UUID userId = UUID.randomUUID();
//        User user = new User(userId, "username", "password", "Old name", null, new Date(), 1.0f);
//
//        ProfileRequest request = new ProfileRequest();
//        request.setName("New Name");
//
//        given(userRepo.findById(userId)).willReturn(Optional.of(user));
//
//        UserResponse result = userService.changeProfile(userId, request);
//
//        assertEquals("New Name", result.name());
//    }
//
//    @Test
//    void givenValidPasswords_whenChangePassword_thenPasswordIsChanged() {
//        TransactionSynchronizationManager.initSynchronization();
//        try {
//            UUID userId = UUID.randomUUID();
//            User user = new User(userId, "testuser", "oldEncodedPassword", "testuser", null, new Date(), 1.0f);
//
//            given(userRepo.findById(userId)).willReturn(Optional.of(user));
//            given(encoder.matches("oldPassword", "oldEncodedPassword")).willReturn(true);
//            given(encoder.encode("newPassword")).willReturn("newEncodedPassword");
//
//            userService.changePassword(userId, "newPassword", "oldPassword");
//
//            // TODO: Fix this shit
//            verify(userRepo).save(user);
//            assertEquals("newEncodedPassword", user.getPassword());
//        } finally {
//            TransactionSynchronizationManager.clearSynchronization();
//        }
//    }
//
//    @Test
//    void givenInvalidPassword_whenChangePassword_thenThrowAccessDeniedException() {
//        UUID userId = UUID.randomUUID();
//        User user = new User(userId, "testuser", "oldEncodedPassword", "testuser", null, new Date(), 1.0f);
//
//        given(userRepo.findById(userId)).willReturn(Optional.of(user));
//        given(encoder.matches("wrongPassword", "oldEncodedPassword")).willReturn(false);
//
//        assertThrows(AccessDeniedException.class, () ->
//                userService.changePassword(userId, "newPassword", "wrongPassword")
//        );
//    }
//
//    @Test
//    void givenUsername_whenPrepareAccount_thenAccountResponseIsReturned() {
//        String username = "testuser";
//        User user = new User(UUID.randomUUID(), username, "oldEncodedPassword", username, null, new Date(), 1.0f);
//
//        given(userRepo.findByUsername(username)).willReturn(Optional.of(user));
//
//        UserResponse result = userService.findByUsername(username);
//
//        assertNotNull(result);
//        assertEquals(username, result.username());
//    }
}
