package com.decade.practice.database.transaction;

import com.decade.practice.core.TokenCredentialService;
import com.decade.practice.core.UserOperations;
import com.decade.practice.core.common.SelfAwareBean;
import com.decade.practice.database.repository.AdminRepository;
import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.event.AccountEventListener;
import com.decade.practice.model.TokenCredential;
import com.decade.practice.model.domain.DefaultAvatar;
import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.model.domain.entity.*;
import com.decade.practice.model.local.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(
      isolation = Isolation.READ_COMMITTED,
      propagation = Propagation.REQUIRES_NEW
)
public class UserService extends SelfAwareBean implements UserOperations {

      private static final String MALE = "male";

      private final UserRepository userRepo;
      private final AdminRepository adminRepo;
      private final PasswordEncoder encoder;
      private final TokenCredentialService credentialService;
      private final List<AccountEventListener> accountListeners;

      @PersistenceContext
      private EntityManager em;

      public UserService(
            UserRepository userRepo,
            AdminRepository adminRepo,
            PasswordEncoder encoder,
            TokenCredentialService credentialService,
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
            User user = new User(username, encoder.encode(password), name, dob, "ROLE_USER", id);
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
                  : DefaultAvatar.INSTANCE;

            return ((UserService) getSelf()).create(
                  username,
                  password,
                  name,
                  new Date(),
                  MALE,
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
            User user = userRepo.getOptimistic(id);
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
            User user = userRepo.getOptimistic(id);
            user.setName(name);
            user.setDob(birthday);
            user.getGender().add(gender);
            user.setAvatar(avatar);
            return user;
      }

      @Override
      public User update(UUID id, ImageSpec avatar) throws OptimisticLockException {
            User user = userRepo.getOptimistic(id);
            user.setAvatar(avatar);
            return user;
      }

      @Override
      public User update(UUID id, String newPassword, String password) throws AccessDeniedException, OptimisticLockException {
            User user = userRepo.getPessimisticWrite(id);
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
            User user = userRepo.getPessimisticWrite(details.getUsername());
            if (!user.getPassword().equals(details.getPassword())) {
                  throw new BadCredentialsException("Password miss matched");
            }
            TokenCredential credential = credentialService.create(user, null);
            return new Account(user, credential);
      }

}