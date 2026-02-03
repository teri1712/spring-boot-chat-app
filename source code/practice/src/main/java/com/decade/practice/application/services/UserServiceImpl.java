package com.decade.practice.application.services;

import com.decade.practice.application.usecases.UserService;
import com.decade.practice.common.SelfAwareBean;
import com.decade.practice.dto.AccountResponse;
import com.decade.practice.dto.ProfileRequest;
import com.decade.practice.dto.SignUpRequest;
import com.decade.practice.dto.UserResponse;
import com.decade.practice.dto.events.UserCreatedEvent;
import com.decade.practice.dto.events.UserPasswordChangedEvent;
import com.decade.practice.persistence.jpa.entities.SyncContext;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl extends SelfAwareBean implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher eventPublisher;

    @PersistenceContext
    private EntityManager em;

    @Override
    public UserResponse createIfNotExists(SignUpRequest signUpRequest, boolean usernameAsIdentifier) throws DataIntegrityViolationException {
        return userRepo.findByUsername(signUpRequest.getUsername()).map(UserResponse::from).orElseGet(() -> create(signUpRequest, usernameAsIdentifier));
    }

    @Override
    public UserResponse create(SignUpRequest signUpRequest, boolean usernameAsIdentifier) {
        UUID id = usernameAsIdentifier ?
                UUID.nameUUIDFromBytes(signUpRequest.getUsername().getBytes()) :
                UUID.randomUUID();

        String encodedPassword = encoder.encode(signUpRequest.getPassword());
        User user = new User();
        user.setId(id);
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encodedPassword);
        user.setDob(signUpRequest.getDob());
        user.setName(signUpRequest.getName());
        user.setAvatar(signUpRequest.getAvatar());
        user.setGender(signUpRequest.getGender());

        SyncContext syncContext = new SyncContext();
        user.setSyncContext(syncContext);
        syncContext.setOwner(user);
        userRepo.save(user);

        eventPublisher.publishEvent(UserCreatedEvent.builder()
                .userId(id)
                .username(signUpRequest.getUsername())
                .name(signUpRequest.getName())
                .dob(signUpRequest.getDob())
                .avatar(signUpRequest.getAvatar())
                .gender(signUpRequest.getGender())
                .build());

        return UserResponse.from(user);
    }

    @Override
    public UserResponse changeProfile(UUID id, ProfileRequest profileRequest) throws OptimisticLockException {
        User user = userRepo.findById(id).orElseThrow();
        if (profileRequest.getName() != null)
            user.setName(profileRequest.getName());
        if (profileRequest.getDob() != null)
            user.setDob(profileRequest.getDob());
        if (profileRequest.getGender() != null)
            user.setGender(profileRequest.getGender());
        if (profileRequest.getAvatar() != null)
            user.setAvatar(profileRequest.getAvatar());
        return UserResponse.from(user);
    }


    @Override
    public UserResponse changePassword(UUID id, String newPassword, String password) throws AccessDeniedException, OptimisticLockException {
        User user = userRepo.findByIdWithPessimisticWrite(id).orElseThrow();

        if (!encoder.matches(password, user.getPassword())) {
            throw new AccessDeniedException("Password miss matched");
        }

        user.setPassword(encoder.encode(newPassword));

        eventPublisher.publishEvent(new UserPasswordChangedEvent(user.getUsername()));
        return UserResponse.from(user);
    }

    @Override
    public AccountResponse prepareAccount(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        return AccountResponse.builder()
                .user(UserResponse.from(user))
                .syncContext(user.getSyncContext())
                .build();
    }

}