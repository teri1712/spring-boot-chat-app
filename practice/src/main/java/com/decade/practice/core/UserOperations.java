package com.decade.practice.core;

import com.decade.practice.model.embeddable.ImageSpec;
import com.decade.practice.model.entity.User;
import com.decade.practice.model.local.Account;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.UUID;

// Java interface for Mockito mocking
public interface UserOperations {

      User create(String username,
                  String password,
                  String name,
                  Date dob,
                  String gender,
                  ImageSpec avatar,
                  boolean usernameAsIdentifier) throws DataIntegrityViolationException;

      User update(UUID id, String name, Date birthday, String gender);

      User update(UUID id, ImageSpec avatar);

      User update(UUID id, String password, String modifierToken) throws AccessDeniedException;

      Account prepareAccount(UserDetails details);

}
