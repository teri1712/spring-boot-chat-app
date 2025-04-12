package com.decade.practice.database;

import com.decade.practice.model.embeddable.ImageSpec;
import com.decade.practice.model.entity.User;
import com.decade.practice.security.model.CredentialModifierInformation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

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

      User update(UUID id, String name, Date birthday, String gender);

      User update(UUID id, ImageSpec avatar);

      User update(CredentialModifierInformation credential, String password) throws AccessDeniedException;

      User validateCredential(CredentialModifierInformation credential) throws AccessDeniedException;
}
