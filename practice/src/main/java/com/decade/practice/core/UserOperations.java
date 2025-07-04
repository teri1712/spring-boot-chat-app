package com.decade.practice.core;

import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.model.local.Account;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.UUID;

public interface UserOperations {

      User create(String username,
                  String password,
                  String name,
                  Date dob,
                  String gender,
                  ImageSpec avatar,
                  boolean usernameAsIdentifier) throws DataIntegrityViolationException;

      User createOauth2User(String username, String name, String picture) throws DataIntegrityViolationException;

      User update(UUID id, String name, Date birthday, String gender);

      User update(UUID id, String name, Date birthday, String gender, ImageSpec avatar);

      User update(UUID id, ImageSpec avatar);

      User update(UUID id, String newPassword, String password) throws AccessDeniedException;

      Account prepareAccount(UserDetails details);

}
