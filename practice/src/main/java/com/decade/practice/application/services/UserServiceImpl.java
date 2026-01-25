package com.decade.practice.application.services;

import com.decade.practice.api.dto.AccountResponse;
import com.decade.practice.api.dto.ProfileRequest;
import com.decade.practice.api.dto.SignUpRequest;
import com.decade.practice.api.dto.UserResponse;
import com.decade.practice.application.events.AccountEventListener;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.common.SelfAwareBean;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl extends SelfAwareBean implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final List<AccountEventListener> accountListeners;

    @PersistenceContext
    private EntityManager em;

    @Override
    public User create(SignUpRequest signUpRequest, boolean usernameAsIdentifier) {
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
        userRepo.save(user);

        for (AccountEventListener listener : accountListeners) {
            listener.beforeAccountCreated(user);
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        for (AccountEventListener listener : accountListeners) {
                            listener.afterAccountCreated(user, status == STATUS_COMMITTED);
                        }
                    }
                });

        return user;
    }

    @Override
    public User changeProfile(UUID id, ProfileRequest profileRequest) throws OptimisticLockException {
        User user = userRepo.findById(id).get();
        user.setName(profileRequest.getName());
        user.setDob(profileRequest.getDob());
        user.setGender(profileRequest.getGender());
        user.setAvatar(profileRequest.getAvatar());
        return user;
    }


    @Override
    public User changePassword(UUID id, String newPassword, String password) throws AccessDeniedException, OptimisticLockException {
        User user = userRepo.findByIdWithPessimisticWrite(id).orElseThrow();

        if (!encoder.matches(password, user.getPassword())) {
            throw new AccessDeniedException("Password miss matched");
        }

        user.setPassword(encoder.encode(newPassword));

        for (AccountEventListener listener : accountListeners) {
            listener.beforePasswordChanged(user);
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        for (AccountEventListener listener : accountListeners) {
                            listener.afterPasswordChanged(user, status == STATUS_COMMITTED);
                        }
                    }
                });

        return user;
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