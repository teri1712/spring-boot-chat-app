package com.decade.practice.application.services;

import com.decade.practice.application.event.AccountEventListener;
import com.decade.practice.application.usecases.TokenService;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.common.SelfAwareBean;
import com.decade.practice.domain.DefaultAvatar;
import com.decade.practice.domain.TokenCredential;
import com.decade.practice.domain.embeddables.ImageSpec;
import com.decade.practice.domain.entities.*;
import com.decade.practice.domain.locals.Account;
import com.decade.practice.domain.repositories.AdminRepository;
import com.decade.practice.domain.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl extends SelfAwareBean implements UserService {

        private final UserRepository userRepo;
        private final AdminRepository adminRepo;
        private final PasswordEncoder encoder;
        private final TokenService credentialService;
        private final List<AccountEventListener> accountListeners;

        @PersistenceContext
        private EntityManager em;

        public UserServiceImpl(
                UserRepository userRepo,
                AdminRepository adminRepo,
                PasswordEncoder encoder,
                TokenService credentialService,
                List<AccountEventListener> accountListeners
        ) {
                this.userRepo = userRepo;
                this.adminRepo = adminRepo;
                this.encoder = encoder;
                this.credentialService = credentialService;
                this.accountListeners = accountListeners;
        }

        @Override
        public User create(
                String username,
                String password,
                String name,
                Date dob,
                String gender,
                ImageSpec avatar,
                boolean usernameAsIdentifier
        ) {
                UUID id = usernameAsIdentifier ?
                        UUID.nameUUIDFromBytes(username.getBytes()) :
                        UUID.randomUUID();
                String encodedPassword = encoder.encode(password);
                User user = new User(username, encodedPassword,
                        name, dob, "ROLE_USER", id);
                user.getGender().add(gender);
                user.setAvatar(avatar);

                Admin admin = adminRepo.get();
                Chat adminChat = new Chat(admin, user);
                WelcomeEvent welcomeEvent = new WelcomeEvent(adminChat, admin, user);
                Edge head = new Edge(user, adminChat, null, welcomeEvent, true);
                welcomeEvent.getEdges().add(head);
                em.persist(welcomeEvent);

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
        public User createOauth2User(
                String username,
                String name,
                String picture
        ) throws DataIntegrityViolationException {
                String password = UUID.randomUUID().toString();
                ImageSpec avatar = (picture != null)
                        ? new ImageSpec(picture, picture, ImageSpec.DEFAULT_WIDTH, ImageSpec.DEFAULT_HEIGHT, ImageSpec.DEFAULT_FORMAT)
                        : DefaultAvatar.getInstance();

                return ((UserServiceImpl) getSelf()).create(
                        username,
                        password,
                        name,
                        new Date(),
                        User.MALE,
                        avatar,
                        false
                );
        }

        @Override
        public User update(
                UUID id,
                String name,
                Date birthday,
                String gender
        ) throws OptimisticLockException {
                User user = userRepo.findById(id).get();
                user.setName(name);
                user.setDob(birthday);
                user.getGender().add(gender);
                return user;
        }

        @Override
        public User update(
                UUID id,
                String name,
                Date birthday,
                String gender,
                ImageSpec avatar
        ) throws OptimisticLockException {
                User user = userRepo.findById(id).get();
                user.setName(name);
                user.setDob(birthday);
                user.getGender().add(gender);
                user.setAvatar(avatar);
                return user;
        }

        @Override
        public User update(UUID id, ImageSpec avatar) throws OptimisticLockException {
                User user = userRepo.findById(id).get();
                user.setAvatar(avatar);
                return user;
        }

        @Override
        public User update(UUID id, String newPassword, String password) throws AccessDeniedException, OptimisticLockException {
                User user = userRepo.findByIdWithPessimisticWrite(id);
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
        public Account prepareAccount(UserDetails details) {
                User user = userRepo.findByUsernameWithPessimisticWrite(details.getUsername());
                if (!user.getPassword().equals(details.getPassword())) {
                        throw new BadCredentialsException("Password miss matched");
                }
                TokenCredential credential = credentialService.create(user, null);
                return new Account(user, credential);
        }

}