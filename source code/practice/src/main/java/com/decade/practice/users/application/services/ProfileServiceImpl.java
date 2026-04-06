package com.decade.practice.users.application.services;

import com.decade.practice.resources.files.api.DownloadPathGenerator;
import com.decade.practice.users.application.ports.in.ProfileService;
import com.decade.practice.users.application.ports.out.TokenStore;
import com.decade.practice.users.application.ports.out.UserRepository;
import com.decade.practice.users.domain.DefaultAvatar;
import com.decade.practice.users.domain.User;
import com.decade.practice.users.domain.UserFactory;
import com.decade.practice.users.domain.UserPasswordPolicy;
import com.decade.practice.users.dto.ProfileRequest;
import com.decade.practice.users.dto.ProfileResponse;
import com.decade.practice.users.dto.SignUpRequest;
import com.decade.practice.users.dto.mapper.UserMapper;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

      private final UserFactory userFactory;
      private final UserRepository users;
      private final TokenStore tokens;
      private final DownloadPathGenerator pathGenerator;
      private final UserMapper userMapper;

      private final UserPasswordPolicy passwordPolicy;


      @Override
      public ProfileResponse createIfNotExists(SignUpRequest signUpRequest, boolean usernameAsIdentifier) throws DataIntegrityViolationException {
            return users.findByUsername(signUpRequest.getUsername())
                      .or(() -> Optional.of(doCreate(signUpRequest, usernameAsIdentifier)))
                      .map(userMapper::map).orElseThrow();

      }

      private User doCreate(SignUpRequest signUpRequest, boolean usernameAsIdentifier) {
            UUID id = usernameAsIdentifier ?
                      UUID.nameUUIDFromBytes(signUpRequest.getUsername().getBytes()) :
                      UUID.randomUUID();

            String username = signUpRequest.getUsername();
            String password = signUpRequest.getPassword();
            String name = signUpRequest.getName();
            Float gender = signUpRequest.getGender();
            Instant dob = signUpRequest.getDob();
            String avatar = Optional.ofNullable(signUpRequest.getAvatar())
                      .orElse(DefaultAvatar.URL);
            User user = userFactory.createUser(id, username, password, name, avatar, dob, gender);
            users.save(user);
            return user;
      }

      @Override
      public ProfileResponse create(SignUpRequest signUpRequest, boolean usernameAsIdentifier) {
            User user = doCreate(signUpRequest, usernameAsIdentifier);
            return userMapper.map(user);
      }

      @Override
      public ProfileResponse changeProfile(UUID id, ProfileRequest profileRequest) throws OptimisticLockException {
            User user = users.findById(id).orElseThrow();
            if (profileRequest.getName() != null)
                  user.changeName(profileRequest.getName());
            if (profileRequest.getDob() != null)
                  user.changeDob(profileRequest.getDob());
            if (profileRequest.getGender() != null)
                  user.changeGender(profileRequest.getGender());
            if (profileRequest.getAvatar() != null) {
                  String avatar = pathGenerator.generateDownload(profileRequest.getAvatar());
                  user.changeAvatar(avatar);
            }
            return userMapper.map(user);
      }


      @Override
      public ProfileResponse changePassword(UUID id, String newPassword, String password) throws AccessDeniedException, OptimisticLockException {
            User user = users.findById(id).orElseThrow();

            passwordPolicy.change(user, password, newPassword);

            users.save(user);
            tokens.evict(user.getUsername());

            return userMapper.map(user);
      }

      @Override
      public ProfileResponse findById(UUID id) {
            return users.findById(id).map(userMapper::map).orElseThrow();
      }

      @Override
      public ProfileResponse findByUsername(String username) {
            return users.findByUsername(username).map(userMapper::map).orElseThrow();
      }

}