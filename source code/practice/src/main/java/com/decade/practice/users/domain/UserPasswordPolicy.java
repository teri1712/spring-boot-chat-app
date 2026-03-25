package com.decade.practice.users.domain;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserPasswordPolicy {

      private final PasswordEncoder passwordEncoder;

      public void change(User user, String oldPassword, String newPassword) {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                  throw new WrongPasswordException();
            }
            user.changePassword(passwordEncoder.encode(newPassword));
      }

}
