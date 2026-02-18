package com.decade.practice.users.application.services;

import com.decade.practice.users.application.ports.in.UserService;
import com.decade.practice.users.application.ports.out.UserRepository;
import com.decade.practice.users.domain.DefaultAvatar;
import com.decade.practice.users.domain.User;
import com.decade.practice.users.domain.UserFactory;
import com.decade.practice.users.domain.UserPasswordPolicy;
import com.decade.practice.users.dto.ProfileRequest;
import com.decade.practice.users.dto.SignUpRequest;
import com.decade.practice.users.dto.UserResponse;
import com.decade.practice.users.dto.mapper.UserMapper;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserFactory userFactory;
    private final UserRepository users;
    private final UserMapper userMapper;

    private final UserPasswordPolicy passwordPolicy;


    @Override
    public UserResponse createIfNotExists(SignUpRequest signUpRequest, boolean usernameAsIdentifier) throws DataIntegrityViolationException {
        return users.findByUsername(signUpRequest.getUsername())
                .map(userMapper::toResponse)
                .orElseGet(() -> create(signUpRequest, usernameAsIdentifier));
    }

    @Override
    public UserResponse create(SignUpRequest signUpRequest, boolean usernameAsIdentifier) {
        UUID id = usernameAsIdentifier ?
                UUID.nameUUIDFromBytes(signUpRequest.getUsername().getBytes()) :
                UUID.randomUUID();

        String username = signUpRequest.getUsername();
        String password = signUpRequest.getPassword();
        String name = signUpRequest.getName();
        Float gender = signUpRequest.getGender();
        Date dob = signUpRequest.getDob();
        String avatar = Optional.ofNullable(signUpRequest.getAvatar())
                .orElse(DefaultAvatar.URL);
        User user = userFactory.createUser(id, username, password, name, avatar, dob, gender);
        users.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse changeProfile(UUID id, ProfileRequest profileRequest) throws OptimisticLockException {
        User user = users.findById(id).orElseThrow();
        if (profileRequest.getName() != null)
            user.changeName(profileRequest.getName());
        if (profileRequest.getDob() != null)
            user.changeDob(profileRequest.getDob());
        if (profileRequest.getGender() != null)
            user.changeGender(profileRequest.getGender());
        if (profileRequest.getAvatar() != null) {
            user.changeAvatar(profileRequest.getAvatar());
        }
        return userMapper.toResponse(user);
    }


    @Override
    public UserResponse changePassword(UUID id, String newPassword, String password) throws AccessDeniedException, OptimisticLockException {
        User user = users.findById(id).orElseThrow();

        passwordPolicy.change(user, password, newPassword);

        users.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse findById(UUID id) {
        return users.findById(id).map(userMapper::toResponse).orElseThrow();
    }

    @Override
    public UserResponse findByUsername(String username) {
        return users.findByUsername(username).map(userMapper::toResponse).orElseThrow();
    }

}